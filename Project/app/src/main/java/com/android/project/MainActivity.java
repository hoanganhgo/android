package com.android.project;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import java.sql.Connection;


public class MainActivity extends Activity {

    private GoogleMap mMap;
    private ConnectionHelper connectionHelper;
    private Connection connection=null;
    private Bussiness bussiness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.login);
        //Khởi tạo lớp Bussiness
        bussiness=new Bussiness();
        //Kết nối cơ sở dữ liệu
        connectionHelper = new ConnectionHelper();
        connection = connectionHelper.connectToServer();
    }


    public void login_Click(View view) {
        EditText edit_user=(EditText)findViewById(R.id.inputUser);
        EditText edit_pass=(EditText)findViewById(R.id.inputPass);
        boolean loginStatus = bussiness.login(edit_user.getText().toString(),edit_pass.getText().toString(),connection);
        if (loginStatus==true)
        {
            setContentView(R.layout.circle_home);
        }
        else{
            TextView login_status=(TextView)findViewById(R.id.loginStatus);
            login_status.setText("Username or Password incorrect!");
        }
    }
}