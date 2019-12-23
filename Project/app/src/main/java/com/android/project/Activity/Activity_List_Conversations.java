package com.android.project.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.project.ModelDatabase.JoinModel;
import com.android.project.ModelDatabase.MessageModel;
import com.android.project.R;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.FirebaseDatabase;

public class Activity_List_Conversations extends AppCompatActivity {

    //private FirebaseListAdapter<MessageModel> adapter;

    private String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations_list);

        // bắt gói Intent bundle từ Home Activity, lấy user name
        Intent callingIntent = getIntent();
        Bundle myBundle = callingIntent.getExtras();
        userName = myBundle.getString("userName");

        //Toast.makeText(getApplicationContext(), "qk ưab k;e;ls::::" + userName, Toast.LENGTH_LONG).show();/////////////////

        // hiển thị tin nhắn
        displayMessagesList();


    }

    // hiển thị tin nhắn
    private void displayMessagesList() {

        ListView listOfConversations = (ListView) findViewById(R.id.list_of_conversations);
        //ConversationAdapter conversationAddapter = new ConversationAdapter(this, ConversationList);

        final FirebaseListAdapter<JoinModel> adapter1 = new FirebaseListAdapter<JoinModel>(this, JoinModel.class,
                R.layout.conversation_item_adapter, FirebaseDatabase.getInstance().getReference().child("Joining").child(userName)
        ) {
            @Override
            protected void populateView(View v, JoinModel model, int position) {
                TextView nameConversation = (TextView) v.findViewById(R.id.nameConversation);
                TextView txtLastMessage = (TextView) v.findViewById(R.id.txtLastMessage);

                nameConversation.setText(model.getCirclename());

                MessageModel lastMess = model.getLastMessage();
                if(lastMess != null)
                {
                    txtLastMessage.setText(String.format("%s: %s", lastMess.getMessageUser(), lastMess.getMessageText()));
                }
                else
                {
                    txtLastMessage.setText("Empty");
                }
            }
        };

        listOfConversations.setAdapter(adapter1);


        // onclick cho các message item

        listOfConversations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String user = userName;
                Intent intent = new Intent(Activity_List_Conversations.this, Activity_Chat.class);
                Bundle bundle = new Bundle();
                bundle.putString("nameCircle", adapter1.getItem(position).getCirclename());
                bundle.putString("userName", userName);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
