package com.android.project.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.project.Bussiness;
import com.android.project.ClassObject.Member;
import com.android.project.ModelDatabase.UserModel;
import com.android.project.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Activity_CircleMember extends Activity {
    private String nameCircle;
    private String userName;
    private TextView tvNameCircle;
    private Button btnInvite;
    private ListView lvMember;
    private List<String> circles = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        nameCircle = bundle.getString("nameCircle");
        userName = bundle.getString("userName");
        //Log.e("Circle17", nameCircle);
        circles = Bussiness.getCircleUserJoinning(userName);

        tvNameCircle = findViewById(R.id.tv_mycircle);
        btnInvite = findViewById(R.id.btnInvite);
        lvMember = findViewById(R.id.list_member);

        tvNameCircle.setText(nameCircle);

        Log.e("AccountChange", userName + " change");

        FirebaseDatabase.getInstance().getReference().child("Account").child("bao").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("AccountChange", userName + " change");
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                for (String circle : circles) {
                    FirebaseDatabase.getInstance().getReference().child("Circles").child("Bao").child("Members").child(userName).setValue(userModel);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentInvite = new Intent(Activity_CircleMember.this, Activity_Invite.class);
                Bundle bundleInvite = new Bundle();
                bundleInvite.putString("nameCircle", nameCircle);
                intentInvite.putExtras(bundleInvite);
                startActivity(intentInvite);
            }
        });
    }
}
