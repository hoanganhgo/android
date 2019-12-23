package com.android.project.Activity;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.project.Bussiness;
import com.android.project.ConnectionHelper;
import com.android.project.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Connection;
import java.util.Objects;


public class MainActivity extends Activity{
    private String TAG_CIRCLE = "Circle17";
    private String SAVE_STATE_TABLE = "saveState";

    //permission
    private static final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 124;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 123;

    private GoogleMap mMap;
    private DatabaseReference myRef=null;
    private ValueEventListener valueEventListener=null;
    public static Connection connection=null;
    public static boolean theFirstLoad=true;

    private EditText edUserName;
    private EditText edPassWord;
    private CheckBox chkbxRememberMe;
    private TextView textStatus;

    private int countAccess;
    private boolean isLogin;
    private boolean isRememberMe;
    private String userName;
    private String passWord;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_login);

        progressBar = (ProgressBar) findViewById(R.id.progressBar_cyclic);

        //Kết nối với các thành phần giao diện
        edUserName = (EditText) findViewById(R.id.inputUser);
        edPassWord = (EditText) findViewById(R.id.inputPass);
        chkbxRememberMe = (CheckBox) findViewById(R.id.cb_rememberme);
        textStatus=(TextView)findViewById(R.id.loginStatus);

        //Cập nhật lại trạng thái trước đó của ứng dụng
        this.updateState();

        //Kết nối cơ sở dữ liệu
        ConnectionHelper connectionHelper = new ConnectionHelper();
        connection = connectionHelper.connectToServer();

        //firebase - get list account in database
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        myRef = database.child("Account");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SplashActivity.listAccount = dataSnapshot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("firebase123", "Failed to read value.", error.toException());
            }
        };

        //GPS ------------------------------------------------------
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            }
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_COARSE_LOCATION);

            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBar.setVisibility(View.INVISIBLE);

        if(isRememberMe){
            edUserName.setText(userName);
            edPassWord.setText(passWord);
            chkbxRememberMe.setChecked(true);
        }
        Log.e(TAG_CIRCLE, "onStart");

        if (!theFirstLoad)
        {
            //Tải lại danh sách Account
            myRef.addListenerForSingleValueEvent(valueEventListener);
        }else{
            theFirstLoad=false;
        }
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
        userName = edUserName.getText().toString();
        passWord = edPassWord.getText().toString();
        if (!isNetworkConnected())
        {
            //Log.e("firebase123","Not Connect");
            textStatus.setText("Check your connection!");
            return;
        }
        else
        {
            if (SplashActivity.listAccount==null)
            {
                textStatus.setText("Connecting to server! Login agian!");
                return;
            }
        }

        progressBar.setVisibility(View.VISIBLE);
        //Kiểm tra tính hợp lệ của tên tài khoản
        if (!Activity_Register.checkUserName(userName))
        {
            textStatus.setText("UserName is not invalid!");
            edUserName.setFocusable(true);
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        //Log.e("firebase123","heher");
        //Kiểm tra sự tồn tại của UserName
        if (!Activity_Register.existUserName(SplashActivity.listAccount,userName))
        {
            //Log.e("firebase123","Not Exist");
            textStatus.setText("UserName is not exist!");
            edUserName.setFocusable(true);
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        //hash password
        passWord=Bussiness.hash(passWord);
        //So khớp mât khẩu
        if (!passWord.contentEquals(Objects.requireNonNull(SplashActivity.listAccount.child(userName).child("password").getValue()).toString()))
        {
            textStatus.setText("Password is incorrect!");
            edPassWord.setFocusable(true);
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }


       // Log.e("firebase123",userName+" "+passWord);

        //Log.e("firebase123","here2");
        Intent intent = new Intent(this, Activity_Home.class);
        Bundle bundle = new Bundle();
        bundle.putString("userName", userName);
        bundle.putString("passWord", passWord);
        intent.putExtras(bundle);
        startActivity(intent);
        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
    }

    public void register_Click(View view) {
        progressBar.setVisibility(View.VISIBLE);
        if (!isNetworkConnected())
        {
            textStatus.setText("Please check your connection!");
            return;
        }

        Intent register = new Intent(this, Activity_Register.class);
        startActivity(register);
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return Objects.requireNonNull(cm).getActiveNetworkInfo() != null && Objects.requireNonNull(cm.getActiveNetworkInfo()).isConnected();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Exit", Toast.LENGTH_SHORT).show();
        finish();
    }
}