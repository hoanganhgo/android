package com.android.project;


import android.app.Activity;
<<<<<<< HEAD
import android.content.Intent;
=======
import android.content.Context;
import android.content.SharedPreferences;
>>>>>>> 466e0ce8bf0c42422b112cc394021cf4684284c4
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import java.sql.Connection;


public class MainActivity extends Activity {
    private String TAG_CIRCLE = "Circle17";
    private String SAVE_STATE_TABLE = "saveState";

    private GoogleMap mMap;
    private ConnectionHelper connectionHelper;
    public static Connection connection=null;

    private Button btnLogin;
    private EditText edUserName;
    private EditText edPassWord;
    private CheckBox chkbxRememberMe;

    private Context context;
    private int countAccess;
    private boolean isLogin;
    private boolean isRememberMe;
    private String userName;
    private String passWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
<<<<<<< HEAD
        setContentView(R.layout.activity_main);
       // getActionBar().hide();
=======
        getActionBar().hide();
        setContentView(R.layout.login);

        //Kết nối với các thành phần giao diện
        btnLogin = (Button)findViewById(R.id.btnLogin);
        edUserName = (EditText)findViewById(R.id.inputUser);
        edPassWord = (EditText)findViewById(R.id.inputPass);
        chkbxRememberMe=(CheckBox)findViewById(R.id.cb_rememberme);

        //Cập nhật lại trạng thái trước đó của ứng dụng
        updateState();

>>>>>>> 466e0ce8bf0c42422b112cc394021cf4684284c4
        //Khởi tạo lớp Bussiness
        //Kết nối cơ sở dữ liệu
        connectionHelper = new ConnectionHelper();
        connection = connectionHelper.connectToServer();
        setContentView(R.layout.login);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG_CIRCLE,"onStart");

<<<<<<< HEAD
    public void login_Click(View view) {
        EditText edit_user=(EditText)findViewById(R.id.inputUser);
        EditText edit_pass=(EditText)findViewById(R.id.inputPass);
        boolean loginStatus = Bussiness.login(edit_user.getText().toString(),edit_pass.getText().toString());
        if (loginStatus)
=======
        if(isRememberMe == true)
>>>>>>> 466e0ce8bf0c42422b112cc394021cf4684284c4
        {
            chkbxRememberMe.setChecked(true);
            edUserName.setText(userName);
            edPassWord.setText(passWord);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG_CIRCLE,"onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG_CIRCLE,"onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveState();
        Log.e(TAG_CIRCLE,"onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG_CIRCLE,"onDestroy");
    }
    public void login_Click(View view) {
        boolean loginStatus = bussiness.login(edUserName.getText().toString(), edPassWord.getText().toString(), connection);
        if (loginStatus == true) {
            setContentView(R.layout.circle_home);
            isLogin = true;
        } else {
            TextView login_status = (TextView) findViewById(R.id.loginStatus);
            login_status.setText("Username or Password incorrect!");
        }
        Log.e(TAG_CIRCLE, "Cliked login");
    }

    private void saveState() {
        SharedPreferences sharedPreferences =
                getSharedPreferences(SAVE_STATE_TABLE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.putBoolean("isLogin", isLogin);

        isRememberMe = chkbxRememberMe.isChecked();

        editor.putBoolean("isRememberMe", isRememberMe);

        userName = edUserName.getText().toString();
        passWord = edPassWord.getText().toString();

        if (isRememberMe == true) {
            editor.putString("userName", userName);
            editor.putString("passWord", passWord);
        }

        editor.putInt("countAccess", countAccess);

        editor.apply();
        Log.e(TAG_CIRCLE, "save state success !!");
    }

    private void updateState() {
        SharedPreferences sharedPreferences =
                getSharedPreferences(SAVE_STATE_TABLE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        isLogin = sharedPreferences.getBoolean("isLogin", false);

        isRememberMe = sharedPreferences.getBoolean("isRememberMe", false);

        if (isRememberMe == true) {
            userName = sharedPreferences.getString("userName", "");
            passWord = sharedPreferences.getString("passWord", "");
        }

        countAccess = sharedPreferences.getInt("countAccess", 0);

        Log.e(TAG_CIRCLE, "update state!!");
    }

    public void register_Click(View view) {
        Intent register=new Intent(this,Register_Activity.class);
        startActivity(register);
    }
}