package com.android.project.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.project.ModelDatabase.JoinModel;
import com.android.project.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Activity_Create_Circle extends Activity {

    private String userName;
    private EditText circleName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_circle);

        circleName =(EditText) findViewById(R.id.input_circleName);

        //Bắt username từ Activity_Home
        Intent callingIntent = getIntent();
        Bundle myBundle = callingIntent.getExtras();
        userName = myBundle.getString("userName");
    }

    public void create_Circle_Click(View view) {
        final String namecircle = circleName.getText().toString();


        final DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Circles").child(namecircle);

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Toast.makeText(Activity_Create_Circle.this, "Circle is exit!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    db.child("admin").setValue(userName);
                    db.child("SOS").setValue("");

                    //Cách mới: tạo một nhánh trong Joining, lấy tên là chính user này
                    //Mỗi mẫu tin sẽ chứa thông tin về tên circle tham gia, ai làm admin, mẫu tin nhắn cuối cùng.

                    FirebaseDatabase.getInstance().getReference().child("Joining").child(userName).child(namecircle).setValue(new JoinModel(namecircle, userName));

                    Toast.makeText(Activity_Create_Circle.this, "Create circle success!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
