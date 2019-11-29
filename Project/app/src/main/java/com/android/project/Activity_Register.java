package com.android.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Activity_Register extends Activity {

    private EditText userName;
    private EditText passWord;
    private EditText rePassWord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        userName=(EditText)findViewById(R.id.register_userName);
        passWord=(EditText)findViewById(R.id.register_passWord);
        rePassWord=(EditText)findViewById(R.id.register_repassWord);
    }


    private boolean checkRePassWord(String pass1, String pass2)
    {
        if (pass1.contentEquals(pass2))
        {
            return true;
        }else
        {
            return false;
        }
    }

    public void register_Click(View view) {
        if (!checkRePassWord(passWord.getText().toString(), rePassWord.getText().toString()))
        {
            rePassWord.setText("");
            rePassWord.setFocusable(true);
        }
        else
        {
            //Lấy lượng pin
            int level=Bussiness.getBatteryPercentage(Activity_Register.this);

            //Lấy vị trí
            MyLocation myLocation=null;
           // MyLocation myLocation=Bussiness.getCurrentLocation(Activity_Register.this);
           // Log.e("hoanganh","GPS: X="+myLocation.getX()+"  Y="+myLocation.getY());

            Bussiness.register(userName.getText().toString(),passWord.getText().toString(), level, null);

            //Chuyển sang màn hình Circle home
            Intent intent = new Intent(this, Activity_Home.class);
            Bundle bundle = new Bundle();
            bundle.putString("userName", userName.getText().toString());
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}