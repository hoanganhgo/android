package com.android.project.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.project.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Activity_Profile extends Activity {
    private String userName;
    private TextView user;

    private Switch share_location;
    private Switch share_speed;
    private Switch share_battery;
    ProgressBar progressBar;

    private DatabaseReference myRef=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user=(TextView)findViewById(R.id.userName);
        share_location=(Switch)findViewById(R.id.share_myLocation);
        share_speed=(Switch)findViewById(R.id.share_mySpeed);
        share_battery=(Switch)findViewById(R.id.share_myBattery);
        progressBar=(ProgressBar)findViewById(R.id.progressBar_cyclic3);

        //Bắt username và sharing status từ Activity_Home
        Intent callingIntent = getIntent();
        Bundle myBundle = callingIntent.getExtras();
        userName = myBundle.getString("userName");
        String status_Location=myBundle.getString("status_Location");
        String status_Battery=myBundle.getString("status_Battery");
        String status_Speed=myBundle.getString("status_Speed");

        //Cài Sharing status lên màn hình
        if (status_Location.contentEquals("0"))
        {
            share_location.setChecked(false);
        }
        if (status_Battery.contentEquals("0"))
        {
            share_battery.setChecked(false);
        }
        if (status_Speed.contentEquals("0"))
        {
            share_speed.setChecked(false);
        }

        //Connect firebase
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        myRef=database.child("Account").child(userName);

        //dán username lên màn hình
        user.setText(userName);
    }


    public void signOut_Click(View view) {
        progressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void share_locaction_change(View view) {
        if (share_location.isChecked())
        {
            myRef.child("share_location").setValue(1);
        }
        else
        {
            myRef.child("share_location").setValue(0);
        }
    }

    public void share_battery_change(View view) {
        if (share_battery.isChecked())
        {
            myRef.child("share_battery").setValue(1);
        }
        else
        {
            myRef.child("share_battery").setValue(0);
        }
    }

    public void share_speed_change(View view) {
        if (share_speed.isChecked())
        {
            myRef.child("share_speed").setValue(1);
        }
        else
        {
            myRef.child("share_speed").setValue(0);
        }
    }
}
