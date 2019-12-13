package com.android.project.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.project.Bussiness;
import com.android.project.Fragment.AddLocation_Fragment;
import com.android.project.Fragment.Invite_Fragment;
import com.android.project.Fragment.Member_Fragment;
import com.android.project.Fragment.SOS_Fragment;
import com.android.project.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

@SuppressLint("Registered")
public class Activity_MyCircle_Home extends AppCompatActivity {
    private AddLocation_Fragment addLocation_fragment;
    private SOS_Fragment sos_fragment;
    private Invite_Fragment invite_fragment;
    private Member_Fragment member_fragment;
    private Activity_Maps maps_fragment;
    private ImageButton btnChat;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mycircle);
        BottomNavigationView bottomNav =  findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //Nhận dữ liệu từ Activity_Home
        Intent callingIntent=getIntent();
        Bundle bundle=callingIntent.getExtras();
        final String circleName=bundle.getString("nameCircle");
        final String userName=bundle.getString("userName");

        btnChat = findViewById(R.id.ib_chat);
        //Read data from database.
        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference sosRef=database.child("Circles");
        //Listen event SOS
        ValueEventListener sos_event=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status= Objects.requireNonNull(dataSnapshot.getValue().toString());
                String parent=dataSnapshot.getKey();
                if (!status.contentEquals("") && !status.contentEquals(userName))
                {
                    Bussiness.notify_SOS(Activity_MyCircle_Home.this, parent, status);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        //Create notify
        sosRef.child(circleName).child("SOS").addValueEventListener(sos_event);

        sos_fragment = new SOS_Fragment(circleName, userName);
        addLocation_fragment = new AddLocation_Fragment();
        invite_fragment = new Invite_Fragment(circleName, userName);
        member_fragment = new Member_Fragment(circleName);
        maps_fragment = new Activity_Maps();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Activity_Maps()).commit();

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("userName", userName);
                bundle.putString("nameCircle", circleName);
                Intent intent = new Intent(Activity_MyCircle_Home.this, Activity_Chat.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_Invite:
                    InitializeFragment(invite_fragment);
                    return true;
                case R.id.nav_Members:
                    InitializeFragment(member_fragment);
                    return true;
                case R.id.nav_Sos:
                    InitializeFragment(sos_fragment);
                    return true;
                case R.id.nav_addLocation:
                    InitializeFragment(addLocation_fragment);
                    return true;
                case R.id.nav_Maps:
                    InitializeFragment(maps_fragment);
                    return true;
            }
            return false;
        }
    };
    private void InitializeFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
