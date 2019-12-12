package com.android.project.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.project.Bussiness;
import com.android.project.ModelDatabase.UserModel;
import com.android.project.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Activity_Invite extends Activity {
    private EditText edUserName;
    private Button btnInvite;
    private String nameCircle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        nameCircle = bundle.getString("nameCircle");

        edUserName = findViewById(R.id.input_newMember);
        btnInvite = findViewById(R.id.btn_addMember);

        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userNameMember = edUserName.getText().toString();
                boolean isSuccess = Bussiness.insertJoiningTable(userNameMember, nameCircle);
                if(isSuccess)
                {
                    FirebaseDatabase.getInstance().getReference().child("Account").child(userNameMember)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    FirebaseDatabase.getInstance().getReference().child("Circles").child(nameCircle).child("Members")
                                            .child(userNameMember).setValue(dataSnapshot.getValue(UserModel.class));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                    Toast.makeText(getApplication(),"Invite Success", Toast.LENGTH_LONG).show();
                    finish();
                }
                else
                {
                    Toast.makeText(getApplication(),"Account not exits", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
