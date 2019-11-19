package com.android.project;

import android.app.Activity;
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

            //Lấy vị trí
            MyLocation myLocation=null;
           // MyLocation myLocation=Bussiness.getCurrentLocation(Register_Activity.this);
           // Log.e("hoanganh","GPS: X="+myLocation.getX()+"  Y="+myLocation.getY());

            Bussiness.register(userName.getText().toString(),passWord.getText().toString(), level, myLocation);
        }
    }
}
