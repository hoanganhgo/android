package com.android.project.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.project.ModelDatabase.JoinModel;
import com.android.project.ModelDatabase.MessageModel;
import com.android.project.R;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Activity_Chat extends AppCompatActivity {

    private FirebaseListAdapter<MessageModel> adapter;

    private String userName;
    private String nameCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // bắt gói Intent bundle từ Home Activity, lấy user name
        Intent callingIntent = getIntent();
        Bundle myBundle = callingIntent.getExtras();
        userName = myBundle.getString("userName");
        nameCircle = myBundle.getString("nameCircle", "Leak");

        Toast.makeText(getApplicationContext(), "nameCircle" + nameCircle, Toast.LENGTH_SHORT).show();

        // hiển thị tin nhắn
        displayChatMessages();

        // gửi tin nhắn
        Button btnSend = (Button)findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.input);

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                final MessageModel newMess = new MessageModel(input.getText().toString(), userName);

                FirebaseDatabase.getInstance()
                        .getReference().child("RoomChat").child(nameCircle)
                        .push()
                        .setValue(newMess);

                FirebaseDatabase.getInstance().getReference().child("Joining").child(userName)
                        .child(nameCircle).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        JoinModel joinModel = dataSnapshot.getValue(JoinModel.class);
                        joinModel.setLastMessage(newMess);
                        FirebaseDatabase.getInstance().getReference().child("Joining").child(userName)
                                .child(nameCircle).setValue(joinModel);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                // Clear the input
                input.setText("");
            }
        });

    }

    // hiển thị tin nhắn
    private void displayChatMessages(){
        ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);

        adapter = new FirebaseListAdapter<MessageModel>(this, MessageModel.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference().child("RoomChat").child(nameCircle)
                ) {
            @Override
            protected void populateView(View v, MessageModel model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);

                // Set their text
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessageTime()));
            }
        };

        listOfMessages.setAdapter(adapter);


    }
}