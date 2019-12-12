package com.android.project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import com.android.project.Activity.MainActivity;
import com.android.project.ClassObject.Circle;
import com.android.project.ClassObject.Dynamic_MyLocation;
import com.android.project.ClassObject.Member;
import com.android.project.ClassObject.MyLocation;
import com.android.project.ClassObject.Static_MyLocation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Bussiness {

    public static boolean register(String userName, String passWord, int battery, MyLocation myLocation, int speed){
        //Khởi tạo thời gian hiện tại
        DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("yyyy-mm-dd hh:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef=database.child("Account").child(userName);
        myRef.child("username").setValue(userName);
        myRef.child("password").setValue(passWord);
        if (myLocation!=null)
        {
            myRef.child("coor_x").setValue(myLocation.getX());
            myRef.child("coor_y").setValue(myLocation.getY());
        }
        else
        {
            myRef.child("coor_x").setValue(0);
            myRef.child("coor_y").setValue(0);
        }
        myRef.child("realtime").setValue((long)(new Date().getTime()));
        myRef.child("battery").setValue(battery);
        myRef.child("speed").setValue(speed);             //viet ham lay speed
        myRef.child("share_location").setValue(1);
        myRef.child("share_battery").setValue(1);
        myRef.child("share_speed").setValue(1);
        return true;
    }

    public static int getBatteryPercentage(Context context) {

        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, iFilter);

        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

        float batteryPct = level / (float) scale;

        return (int) (batteryPct * 100);
    }

    public static void notify_SOS(final Context context, String circleName, String member)
    {
        AlertDialog.Builder alert_sos=new AlertDialog.Builder(context);
        alert_sos.setTitle("SOS");
        alert_sos.setMessage("I am "+member+". We join "+circleName+" together. Please help me now!");
        alert_sos.setIcon(R.drawable.icon_sos);

// Setting Negative "NO" Btn
        alert_sos.setNegativeButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        try{
            alert_sos.show();
        }catch (Exception ignored)
        {
            //Nothing
        }
    }

    public static List<String> getCircleUserJoinning(String username) {
        List<String> Circles = new ArrayList<String>();

        try {
            //truy xuất cơ sở dữ liệu sql
            Statement statement = MainActivity.connection.createStatement();
            ResultSet resultSet;

            //Tìm ID của circle và Admin theo tên của Circle
            resultSet = statement.executeQuery("Select NameCircle from Joining where UserName = '"+username+"'");

            while (resultSet.next()) {
                Circles.add(resultSet.getString(1));
            }
            Log.e("getCircleUserJoinning", "getCircleUserJoinning success!");
            return Circles;
        } catch (SQLException e) {
            Log.e("getCircleUserJoinning", "getCircleUserJoinning fail");
            e.printStackTrace();
            return null;
        }
    }

    public static boolean insertJoiningTable(String username, String namecircle){
        try {
            //truy xuất cơ sở dữ liệu sql
            Statement statement = MainActivity.connection.createStatement();

            Log.e("insertJoiningTable", "username: "+username+", namecircle: "+namecircle);

            statement.executeUpdate("INSERT INTO Joining (NameCircle, UserName) " +
                    "VALUES ('"+namecircle+"', '"+username+"');");

            Log.e("insertJoiningTable", "insertJoiningTable success");
            return true;
        } catch (SQLException e) {
            Log.e("insertJoiningTable", "insertJoiningTable fail");
            e.printStackTrace();
            return false;
        }
    }
}
