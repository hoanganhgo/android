package com.android.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Register_Activity extends Activity {

    private EditText userName;
    private EditText passWord;
    private EditText rePassWord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

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
            int level=Bussiness.getBatteryPercentage(Register_Activity.this);

            //Đăng ký
            Bussiness.register(userName.getText().toString(),passWord.getText().toString(), level, MainActivity.myLocation);

            //Chuyển sang màn hình Circle home
            Intent intent = new Intent(this, Home_Activity.class);
            Bundle bundle = new Bundle();
            bundle.putString("userName", userName.getText().toString());
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
