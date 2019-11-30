package com.android.project;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

@SuppressLint("Registered")
public class Activity_MyCircle_Home extends AppCompatActivity {
    private AddLocation_Fragment addLocation_fragment;
    private SOS_Fragment sos_fragment;
    private Invite_Fragment invite_fragment;
    private Member_Fragment member_fragment;
    private Activity_Maps maps_fragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mycircle);
        BottomNavigationView bottomNav =  findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        sos_fragment = new SOS_Fragment();
        addLocation_fragment = new AddLocation_Fragment();
        invite_fragment = new Invite_Fragment();
        member_fragment = new Member_Fragment();
        maps_fragment = new Activity_Maps();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Activity_Maps()).commit();

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
