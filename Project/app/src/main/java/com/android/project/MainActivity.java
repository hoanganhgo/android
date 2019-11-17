package com.android.project;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import java.sql.Connection;


public class MainActivity extends Activity {

    private GoogleMap mMap;
    private ConnectionHelper connectionHelper;
    public static Connection connection=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // getActionBar().hide();
        //Khởi tạo lớp Bussiness
        //Kết nối cơ sở dữ liệu
        connectionHelper = new ConnectionHelper();
        connection = connectionHelper.connectToServer();
        setContentView(R.layout.login);
    }


    public void login_Click(View view) {
        EditText edit_user=(EditText)findViewById(R.id.inputUser);
        EditText edit_pass=(EditText)findViewById(R.id.inputPass);
        boolean loginStatus = Bussiness.login(edit_user.getText().toString(),edit_pass.getText().toString());
        if (loginStatus)
        {
            setContentView(R.layout.circle_home);
        }
        else{
            TextView login_status=(TextView)findViewById(R.id.loginStatus);
            login_status.setText("Username or Password incorrect!");
        }
    }

    public void register_Click(View view) {
        Intent register=new Intent(this,Register_Activity.class);
        startActivity(register);
    }
}