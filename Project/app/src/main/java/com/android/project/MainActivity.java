package com.android.project;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public class MainActivity extends Activity {
    private String TAG_CIRCLE = "Circle17";
    private String SAVE_STATE_TABLE = "saveState";

    private GoogleMap mMap;
    private ConnectionHelper connectionHelper;
    public static Connection connection = null;

    private Button btnLogin;
    private EditText edUserName;
    private EditText edPassWord;
    private CheckBox chkbxRememberMe;
    private TextView textStatus;

    private int countAccess;
    private boolean isLogin;
    private boolean isRememberMe;
    private String userName;
    private String passWord;
    private View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
<<<<<<< HEAD
=======

        // getActionBar().hide();

        getActionBar().hide();
>>>>>>> 3d9b823f4c7d15193138015b28506640205c8e34
        setContentView(R.layout.login);

        //Kết nối với các thành phần giao diện
        btnLogin = (Button) findViewById(R.id.btnLogin);
        edUserName = (EditText) findViewById(R.id.inputUser);
        edPassWord = (EditText) findViewById(R.id.inputPass);
        chkbxRememberMe = (CheckBox) findViewById(R.id.cb_rememberme);
        textStatus=(TextView)findViewById(R.id.loginStatus);

        //Cập nhật lại trạng thái trước đó của ứng dụng
        this.updateState();

        //Kết nối cơ sở dữ liệu
        connectionHelper = new ConnectionHelper();
        connection = connectionHelper.connectToServer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(isRememberMe){
            edUserName.setText(userName);
            edPassWord.setText(passWord);
            chkbxRememberMe.setChecked(true);
        }
        Log.e(TAG_CIRCLE, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG_CIRCLE, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG_CIRCLE, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveState();
        Log.e(TAG_CIRCLE, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG_CIRCLE, "onDestroy");
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

    public void login_Click(View view) {
        this.view = view;
     //   Log.e(TAG_CIRCLE, "Clicked");
        userName = edUserName.getText().toString();
        passWord = edPassWord.getText().toString();

        if (connection==null)
        {
            textStatus.setText("Please check your connection!");
            return;
        }

        boolean loginStatus = Bussiness.login(userName, passWord);
        /*Test
        Circle circle = new Circle("Circletest", new Member(userName));
        Bussiness.insertCircleToDatabase(circle);
        Bussiness.deleteCircleToDatabase(circle);*/
        List<String> list = Bussiness.getListCircleFromDatabase(userName);

        for(int i=0; i<list.size(); i++){
            Log.e("CircleName: ", list.get(i));
        }

        if (loginStatus) {
            Intent intent = new Intent(this, Home_Activity.class);
            Bundle bundle = new Bundle();
            bundle.putString("userName", userName);
            bundle.putString("passWord", passWord);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            textStatus.setText("Username or Password is incorrect!");
        }
    }

    public void register_Click(View view) {
        if (connection==null)
        {
            textStatus.setText("Please check your connection!");
            return;
        }

        Intent register = new Intent(this, Register_Activity.class);
        startActivity(register);
    }
}