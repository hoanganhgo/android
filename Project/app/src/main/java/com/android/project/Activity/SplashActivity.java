package com.android.project.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.project.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SplashActivity extends Activity {
    public static DataSnapshot listAccount = null;
    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);
        textView=findViewById(R.id.notConnect);

        Thread welcomeThread = new Thread() {

            @Override
            public void run() {
                try {
                    super.run();
                    if (!isNetworkConnected())
                    {
                        textView.setText("Please check your connection!");
                    }else
                    {
                        sleep(2000);            //Nếu không sleep thì animation không hoạt động
                    }
                    while (!isNetworkConnected())
                    {
                        sleep(500);            //Nếu chưa kết nối internet thì không vào được
                    }
                    //firebase - get list account in database
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference myRef = database.child("Account");
                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            listAccount = dataSnapshot;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Failed to read value
                            Log.w("firebase123", "Failed to read value.", error.toException());
                        }
                    };
                    myRef.addListenerForSingleValueEvent(valueEventListener);

                } catch (Exception e) {

                } finally {                     //Khối lệnh finally luôn được thực thi bất chấp ngoại lệ xảy ra

                    Intent i = new Intent(SplashActivity.this,
                            MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        };
        welcomeThread.start();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return Objects.requireNonNull(cm).getActiveNetworkInfo() != null && Objects.requireNonNull(cm.getActiveNetworkInfo()).isConnected();
    }
}
