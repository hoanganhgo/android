package com.android.project.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.project.Bussiness;
import com.android.project.ClassObject.Circle;
import com.android.project.ClassObject.Member;
import com.android.project.ModelDatabase.UserModel;
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

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Circles").child(namecircle);
        db.child("admin").setValue(userName);
        db.child("SOS").setValue("");

        Bussiness.insertJoiningTable(userName, namecircle);
        Toast.makeText(getApplicationContext(), "create ok", Toast.LENGTH_SHORT).show();
        FirebaseDatabase.getInstance().getReference().child("Account").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("TestCreate", "is running");

                for(DataSnapshot dt : dataSnapshot.getChildren())
                {
                    UserModel usermodel = dt.getValue(UserModel.class);

                    Log.e("TestCreate", usermodel.getUsername());

                    if(usermodel.getUsername().contentEquals(userName))
                    {
                        FirebaseDatabase.getInstance().getReference().child("Circles")
                                .child(namecircle).child("Members").child(userName).setValue(usermodel);

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Toast.makeText(this, "Create circle success!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
