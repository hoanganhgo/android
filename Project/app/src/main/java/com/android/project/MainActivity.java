package com.android.project;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Connection;
import java.util.List;
import java.util.Objects;


public class MainActivity extends Activity implements LocationListener {
    private String TAG_CIRCLE = "Circle17";
    private String SAVE_STATE_TABLE = "saveState";

    //permission
    private static final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 124;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 123;

    private GoogleMap mMap;
    private ConnectionHelper connectionHelper;
    public static Connection connection = null;
    public static MyLocation myLocation = null;
    private DataSnapshot listAccount = null;
    private DatabaseReference myRef=null;
    private ValueEventListener valueEventListener=null;
    public static int speed=0;


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
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_login);

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

        //firebase - get list account in database
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        myRef = database.child("Account");
        valueEventListener = new ValueEventListener() {
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        assert locationManager != null;
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,  this);
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

        //Tải lại danh sách Account
        myRef.addListenerForSingleValueEvent(valueEventListener);
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

        if (!isNetworkConnected() && listAccount==null)
        {
            //Log.e("firebase123","Not Connect");
            textStatus.setText("Check your connection!");
            return;
        }

        //Kiểm tra tính hợp lệ của tên tài khoản
        if (!Activity_Register.checkUserName(userName))
        {
            textStatus.setText("UserName is not invalid!");
            edUserName.setFocusable(true);
            return;
        }

        //Log.e("firebase123","heher");
        //Kiểm tra sự tồn tại của UserName
        if (!Activity_Register.existUserName(listAccount,userName))
        {
            //Log.e("firebase123","Not Exist");
            textStatus.setText("UserName is not exist!");
            edUserName.setFocusable(true);
            return;
        }

        //So khớp mât khẩu
        if (!passWord.contentEquals(Objects.requireNonNull(listAccount.child(userName).child("PassWord").getValue()).toString()))
        {
            textStatus.setText("Password is incorrect!");
            edPassWord.setFocusable(true);
            return;
        }
       // Log.e("firebase123",userName+" "+passWord);
        List<String> list = Bussiness.getListCircleFromDatabase(userName);
        //Log.e("firebase123","here1");
        for(int i=0; i<list.size(); i++){
            Log.e("CircleName: ", list.get(i));
        }
        //Log.e("firebase123","here2");
        Intent intent = new Intent(this, Activity_Home.class);
        Bundle bundle = new Bundle();
        bundle.putString("userName", userName);
        bundle.putString("passWord", passWord);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void register_Click(View view) {
        if (!isNetworkConnected())
        {
            textStatus.setText("Please check your connection!");
            return;
        }

        Intent register = new Intent(this, Activity_Register.class);
        startActivity(register);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("LocationGPS", "lat: " + location.getLatitude());
        Log.e("LocationGPS", "lng: " + location.getLongitude());
        myLocation=new MyLocation((float)location.getLatitude(),(float)location.getLongitude());

        this.speed=(int)Math.round(location.getSpeed()*3.6);
        Log.e("LocationGPS","Speed: " + this.speed + "km/h");

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return Objects.requireNonNull(cm).getActiveNetworkInfo() != null && Objects.requireNonNull(cm.getActiveNetworkInfo()).isConnected();
    }
}