package com.android.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Profile_Activity extends Activity {
    private String userName;
    private TextView user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        user=(TextView)findViewById(R.id.userName);

        //Bắt username từ Home_Activity
        Intent callingIntent = getIntent();
        Bundle myBundle = callingIntent.getExtras();
        userName = myBundle.getString("userName");

        //dán username lên màn hình
        user.setText(userName);
    }

    public void signOut_Click(View view) {
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
