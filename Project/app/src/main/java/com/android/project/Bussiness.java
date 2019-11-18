package com.android.project;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.util.Log;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;


public class Bussiness {

    //Nếu thông tin đăng nhập đúng sẽ trả về true
    public static boolean login(String userName, String passWord) {
        try {
            //truy xuất cơ sở dữ liệu sql
            Statement statement = MainActivity.connection.createStatement();
            ResultSet resultSet = statement.executeQuery("Select PassWord From Account Where UserName=" + "'" + userName + "'");
            resultSet.next();
            //Lấy mật khẩu tương ứng với username
            String truePassWord = resultSet.getString(1);
            if (truePassWord.contentEquals(passWord)) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean register(String userName, String passWord, int battery, MyLocation myLocation) {
        try {
            //truy xuất cơ sở dữ liệu sql
            Statement statement = MainActivity.connection.createStatement();
            //Chọn ID cho tài khoản mới
            ResultSet resultSet = statement.executeQuery("Select MAX(ID_Account) from Account");
            resultSet.next();
            int newID = resultSet.getInt(1) + 1;
            //Khởi tạo thời gian hiện tại
            DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("yyyy-mm-dd hh:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            //Đưa dữ liệu lên Database
            statement.executeUpdate("Insert into Account (ID_Account, UserName, PassWord, Coordinates_X, Coordinates_Y, RealTime, Battery, Speed, Share_Location, Share_Battery, Share_Speed) "
                    + " values ("+newID+", '"+userName+"', '"+passWord+"', 0, 0, '"+now+"', "+battery+", 50, 1 , 1, 1)");

            //Log.e("hoanganh", "Insert success!");
            //Log.e("hoanganh", now.toString());
            //Log.e("hoanganh", "Pin-> " + battery);
            //Log.e("hoanganh", "GPS: X=" + myLocation.getX() + "  Y=" + myLocation.getY());
            return true;
        } catch (SQLException e) {
           // Log.e("Circle17", "Insert fail");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean insertCircleToDatabase(Circle circle) {
        try {
            //truy xuất cơ sở dữ liệu sql
            Statement statement = MainActivity.connection.createStatement();

            //Chọn ID cho Circle mới
            ResultSet resultSet = statement.executeQuery("Select MAX(ID_Circle) from Circle");
            resultSet.next();
            int newCircleID = resultSet.getInt(1) + 1;

            Log.e("Circle17", "Circle ID: " + Integer.toString(newCircleID));
            Log.e("Circle17", "UserName: " + circle.getAdmin().getUserName());

            //Lấy ID của của Admin theo userName
            resultSet = statement.executeQuery("Select ID_Account from Account where UserName = '" + circle.getAdmin().getUserName()+"'");
            resultSet.next();
            int ID_Admin = resultSet.getInt(1);

            Log.e("Circle17", "Admin ID: " + Integer.toString(ID_Admin));

            //Đưa dữ liệu lên Database
            statement.executeUpdate("Insert into Circle (ID_Circle, CircleName, Admin) "
                    + " values (" + newCircleID + ", '" + circle.getCircleName() + "', " + ID_Admin + ")");
            //resultSet.next();
            Log.e("hoanganh", "Insert success!");
            return true;
        } catch (SQLException e) {
            Log.e("hoanganh", "Insert fail");
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteCircleToDatabase(Circle circle) {
        try {
            //truy xuất cơ sở dữ liệu sql
            Statement statement = MainActivity.connection.createStatement();

            ResultSet resultSet;
            resultSet = statement.executeQuery("Select ID_Account from Account where UserName = '" + circle.getAdmin().getUserName()+"'");
            resultSet.next();
            int ID_Admin = resultSet.getInt(1);

            //Đưa dữ liệu lên Database
            statement.executeUpdate("delete from Circle where CircleName = '"
                    + circle.getCircleName() + "' and Admin = "
                    + ID_Admin);
            //resultSet.next();
            Log.e("Circle17", "delete success!");
            return true;
        } catch (SQLException e) {
            Log.e("Circle17", "delete fail");
            e.printStackTrace();
            return false;
        }
    }

    public static int getBatteryPercentage(Context context) {

        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, iFilter);

        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

        float batteryPct = level / (float) scale;

        return (int) (batteryPct * 100);
    }

    //@SuppressLint("MissingPermission")
    public static MyLocation getCurrentLocation(Context context) {
        /*MyLocation result=null;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            android.location.Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocationGPS != null) {
                result=new MyLocation((float)lastKnownLocationGPS.getLatitude(),(float)lastKnownLocationGPS.getLongitude());
            } else {
                lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                result=new MyLocation((float)lastKnownLocationGPS.getLatitude(),(float)lastKnownLocationGPS.getLongitude());
            }
            return result;
        } else {
            return null;
        }*/
        LocationManager locationManager = null;
        MyLocation locationListener = new MyLocation();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
     //   locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        //Log.e("hoanganh", "OK!");
        return locationListener;
    }

    public static List<String> getListCircleFromDatabase(String username) {
        List<String> listNameCircle = new ArrayList<String>();

        try {
            //truy xuất cơ sở dữ liệu sql
            Statement statement = MainActivity.connection.createStatement();
            ResultSet resultSet;
            resultSet = statement.executeQuery("Select ID_Account from Account where UserName = '" + username + "'");
            resultSet.next();
            int ID_Account = resultSet.getInt(1);

            Log.e("Circle17", "ID Account: " + Integer.toString(ID_Account));

            resultSet = statement.executeQuery("Select ID_Circle from Joining");// where ID_Account = " + ID_Account);
/*            resultSet.next();
            int ID_Circle = resultSet.getInt(1);*/
            ResultSet cur;
            int ID_Circle;
            while(resultSet.next())
            {

                ID_Circle = resultSet.getInt(1);
                Log.e("Circle17", "ID Circle: " + Integer.toString(ID_Circle));
                cur = statement.executeQuery("Select CircleName from Circle where ID_Circle = " + ID_Circle);
                cur.next();
                listNameCircle.add(cur.getString(1));
                Log.e("Circle17", "Circle name: " + cur.getString(1));

            }

            Log.e("Circle17", "Query success!");
        } catch (SQLException e) {
            Log.e("Circle17", "Query fail");
            e.printStackTrace();
        }
        return listNameCircle;
    }
}
