package com.android.project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.facebook.FacebookSdk.getApplicationContext;


public class Bussiness {

    public static boolean register(String userName, String passWord, int battery, MyLocation myLocation, int speed){
        //Khởi tạo thời gian hiện tại
        DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("yyyy-mm-dd hh:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef=database.child("Account").child(userName);
        myRef.child("PassWord").setValue(passWord);
        if (myLocation!=null)
        {
            myRef.child("Coordinates_X").setValue(myLocation.getX());
            myRef.child("Coordinates_Y").setValue(myLocation.getY());
        }
        else
        {
            myRef.child("Coordinates_X").setValue(0);
            myRef.child("Coordinates_Y").setValue(0);
        }
        myRef.child("RealTime").setValue(now.toString());
        myRef.child("Battery").setValue(battery);
        myRef.child("Speed").setValue(speed);             //viet ham lay speed
        myRef.child("Share_Location").setValue(1);
        myRef.child("Share_Battery").setValue(1);
        myRef.child("Share_Speed").setValue(1);
        return true;
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

            insertMember(circle.getCircleName(),circle.getAdmin().getUserName());

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

            resultSet = statement.executeQuery("Select ID_Circle from Joining where ID_Account = " + ID_Account);
/*            resultSet.next();
            int ID_Circle = resultSet.getInt(1);*/

            List<Integer> ID_Circle = new ArrayList<Integer>();
            while(resultSet.next())
            {
                ID_Circle.add(resultSet.getInt(1));
                //Log.e("Circle17", "ID Circle: " + Integer.toString(ID_Circle));
            }

            for(int i=0; i<ID_Circle.size(); i++)
            {
                String idCircleString = Integer.toString(ID_Circle.get(i));
                Log.e("Circle17", "ID Circle: " + idCircleString);
                ResultSet cur = statement.executeQuery("Select CircleName from Circle where ID_Circle = " + idCircleString);
                cur.next();
                String nameCircle = cur.getString(1);
                listNameCircle.add(nameCircle);
                Log.e("Circle17", "Name Circle: " + nameCircle);
            }

            Log.e("Circle17", "Query success!");
        } catch (SQLException e) {
            Log.e("Circle17", "Query fail");
            e.printStackTrace();
        }
        return listNameCircle;
    }

    public static List<String> getListUserFromDatabase(String nameCircle) {
        List<String> listNameAccount = new ArrayList<String>();

        Log.e("Circle17", "Get list username");
        try {
            //truy xuất cơ sở dữ liệu sql
            Statement statement = MainActivity.connection.createStatement();
            ResultSet resultSet;
            resultSet = statement.executeQuery("Select ID_Circle from Circle where CircleName = '" + nameCircle + "'");
            resultSet.next();
            int ID_Circle = resultSet.getInt(1);

            Log.e("Circle17", "ID Circle: " + Integer.toString(ID_Circle));

            resultSet = statement.executeQuery("Select ID_Account from Joining where ID_Circle = " + ID_Circle);
/*            resultSet.next();
            int ID_Circle = resultSet.getInt(1);*/

            List<Integer> ID_Account = new ArrayList<Integer>();
            while(resultSet.next())
            {
                ID_Account.add(resultSet.getInt(1));
                //Log.e("Circle17", "ID Circle: " + Integer.toString(ID_Circle));
            }

            for(int i=0; i<ID_Account.size(); i++)
            {
                String idAccountString = Integer.toString(ID_Account.get(i));
                Log.e("Circle17", "ID Account: " + idAccountString);
                ResultSet cur = statement.executeQuery("Select UserName from Account where ID_Account = " + idAccountString);
                cur.next();
                String nameAccount = cur.getString(1);
                listNameAccount.add(nameAccount);
                Log.e("Circle17", "Name Account: " + nameCircle);
            }

            Log.e("Circle17", "Query success!");
        } catch (SQLException e) {
            Log.e("Circle17", "Query fail");
            e.printStackTrace();
        }
        return listNameAccount;
    }

    public static Circle loadCircle(String nameCircle) {
        Log.e("Circle17", "Load circle");
        Circle circle;
        Member admin = new Member("Bao test");
        try {
            //truy xuất cơ sở dữ liệu sql
            Statement statement = MainActivity.connection.createStatement();
            ResultSet resultSet;

            //Tìm ID của circle và Admin theo tên của Circle
            resultSet = statement.executeQuery("Select ID_Circle, Admin from Circle where CircleName = '" + nameCircle + "'");
            resultSet.next();
            int ID_Circle = resultSet.getInt(1);
            int ID_Admin = resultSet.getInt(2);

            //Tìm các thành viên tham gia memmber
            Log.e("Circle17", "ID Circle: " + Integer.toString(ID_Circle));

            //Lấy các thành viên tham gia trong circle
            resultSet = statement.executeQuery("Select ID_Account from Joining where ID_Circle = " + ID_Circle);

            //Lấy danh sách các ID của Account
            List<Integer> ID_Account = new ArrayList<Integer>();
            while (resultSet.next()) {
                ID_Account.add(resultSet.getInt(1));
            }

            //Dựa vào danh sách ID các Account, ta tạo ra danh sách các Member
            List<Member> list_member = new ArrayList<Member>();
            for (int i = 0; i < ID_Account.size(); i++) {
                String idAccountString = Integer.toString(ID_Account.get(i));
                Log.e("Circle17", "ID Account: " + idAccountString);
                Log.e("Circle17", "start");
                ResultSet cur = statement.executeQuery("Select *" +
                        " from Account where ID_Account = " + idAccountString);
                cur.next();
                String userName = cur.getString(2);
                Log.e("Circle17", userName);
                float coor_x = cur.getFloat(4);
                float coor_y = cur.getFloat(5);
                Time time = cur.getTime(6);

                int battery = cur.getInt(7);
                int speed = cur.getInt(8);

                boolean isShareLocation = cur.getInt(9) == 1 ? true : false;
                boolean isShareBaterry = cur.getInt(10) == 1 ? true : false;
                boolean isShareSpeed = cur.getInt(11) == 1 ? true : false;

                if (ID_Account.get(i) == ID_Admin) {
                    Log.e("Circle17", "ID_Account = ID_Admin");
                    admin = new Member(userName, new Dynamic_MyLocation(coor_x, coor_y, time), battery, speed, isShareLocation, isShareBaterry, isShareSpeed);
                } else {
                    Member member = new Member(userName, new Dynamic_MyLocation(coor_x, coor_y, time), battery, speed, isShareLocation, isShareBaterry, isShareSpeed);
                    list_member.add(member);
                }
            }

            Log.e("Circle17", "Load account success");

            resultSet = statement.executeQuery("Select * from Static_Location where ID_Circle = " + Integer.toString(ID_Circle));

            List<Static_MyLocation> list_Static_Location = new ArrayList<Static_MyLocation>();
            while (resultSet.next()) {
                float s_coor_x = resultSet.getFloat(3);
                float s_coor_y = resultSet.getFloat(4);
                Time checkIn = resultSet.getTime(5);
                Time checkOut = resultSet.getTime(6);
                Log.e("Circle17", "Time checkin: " + checkIn.toString());
                list_Static_Location.add(new Static_MyLocation(s_coor_x, s_coor_y, checkIn, checkOut));
            }
            //admin = new Member();
            circle = new Circle(nameCircle, list_member, admin, list_Static_Location);
            Log.e("Circle17", "Query success!");
            return circle;
        } catch (SQLException e) {
            Log.e("Circle17", "Query fail");
            e.printStackTrace();
            return null;
        }
    }

    public static boolean insertMember(String nameCircle, String userName) {
        try {
            //truy xuất cơ sở dữ liệu sql
            Statement statement = MainActivity.connection.createStatement();
            ResultSet resultSet = statement.executeQuery("Select MAX(ID_Join) from Joining");
            resultSet.next();
            int newIDJoin = resultSet.getInt(1) + 1;

            resultSet = statement.executeQuery("Select ID_Account from Account where UserName = '" + userName + "'");
            resultSet.next();
            int ID_Account = resultSet.getInt(1);

            resultSet = statement.executeQuery("Select ID_Circle from Circle where CircleName = '" + nameCircle + "'");
            resultSet.next();
            int ID_Circle = resultSet.getInt(1);

            Log.e("Circle17", "ID Account: " + Integer.toString(ID_Account));

            statement.executeUpdate("Insert into Joining (ID_Join, ID_Circle, ID_Account) " +
                    " values (" + newIDJoin + ", " + ID_Circle + ", " + ID_Account + ")");

            Log.e("Circle17", "Insert member success!");

            return true;
        } catch (SQLException e) {
            Log.e("Circle17", "Insert member fail");
            e.printStackTrace();

            return false;
        }
    }

    public static boolean deleteMember(String nameCircle, String userName) {
        try {
            //truy xuất cơ sở dữ liệu sql
            Statement statement = MainActivity.connection.createStatement();
            ResultSet resultSet = statement.executeQuery("Select MAX(ID_Join) from Joining");
            resultSet.next();
            int newIDJoin = resultSet.getInt(1) + 1;

            resultSet = statement.executeQuery("Select ID_Account from Account where UserName = '" + userName + "'");
            resultSet.next();
            int ID_Account = resultSet.getInt(1);

            resultSet = statement.executeQuery("Select ID_Circle from Circle where CircleName = '" + nameCircle + "'");
            resultSet.next();
            int ID_Circle = resultSet.getInt(1);

            Log.e("Circle17", "ID Account: " + Integer.toString(ID_Account));

            statement.executeUpdate("delete from Joining where ID_Circle = " + ID_Circle + " and ID_Account = " + ID_Account);

            Log.e("Circle17", "Delete member success!");

            return true;
        } catch (SQLException e) {
            Log.e("Circle17", "Delete member fail");
            e.printStackTrace();

            return false;
        }
    }

    public static boolean insertStaticLocation(String nameCircle, Static_MyLocation static_myLocation) {
        try {
            //truy xuất cơ sở dữ liệu sql
            Statement statement = MainActivity.connection.createStatement();
            ResultSet resultSet = statement.executeQuery("Select MAX(ID_Location) from Static_Location");
            resultSet.next();
            int newIDLocation = resultSet.getInt(1) + 1;

            resultSet = statement.executeQuery("Select ID_Circle from Circle where CircleName = '" + nameCircle + "'");
            resultSet.next();
            int ID_Circle = resultSet.getInt(1);

            Log.e("Circle17", "insert Static Loaction");
            statement.executeUpdate("Insert into Static_Location (ID_Location) " + //, ID_Circle, Coordinates_X, Coordinates_Y) " +
                    " values (" + newIDLocation + ")");//", " + ID_Circle + ", " + static_myLocation.getX() + ", "
            //+ static_myLocation.getY() + ", '" + static_myLocation.getCheckIn() + "', '" + static_myLocation.getCheckOut() + "')");
            Log.e("Circle17", "Delete member success!");

            return true;
        } catch (SQLException e) {
            Log.e("Circle17", "Delete member fail");
            e.printStackTrace();

            return false;
        }
    }

    public static List<Member> getMembers(String nameCircle) {
        Log.e("Circle17", "Get members");

        List<Member> list_member = new ArrayList<Member>();
        try {
            //truy xuất cơ sở dữ liệu sql
            Statement statement = MainActivity.connection.createStatement();
            ResultSet resultSet;

            //Tìm ID của circle và Admin theo tên của Circle
            resultSet = statement.executeQuery("Select ID_Circle from Circle where CircleName = '" + nameCircle + "'");
            resultSet.next();
            int ID_Circle = resultSet.getInt(1);

            //Tìm các thành viên tham gia memmber
            Log.e("Circle17", "ID Circle: " + Integer.toString(ID_Circle));

            //Lấy các thành viên tham gia trong circle
            resultSet = statement.executeQuery("Select ID_Account from Joining where ID_Circle = " + ID_Circle);

            //Lấy danh sách các ID của Account
            List<Integer> ID_Account = new ArrayList<Integer>();
            while (resultSet.next()) {
                ID_Account.add(resultSet.getInt(1));
            }

            //Dựa vào danh sách ID các Account, ta tạo ra danh sách các Member

            for (int i = 0; i < ID_Account.size(); i++) {
                String idAccountString = Integer.toString(ID_Account.get(i));
                Log.e("Circle17", "ID Account: " + idAccountString);
                Log.e("Circle17", "start");
                ResultSet cur = statement.executeQuery("Select *" +
                        " from Account where ID_Account = " + idAccountString);
                cur.next();
                String userName = cur.getString(2);
                Log.e("Circle17", userName);
                float coor_x = cur.getFloat(4);
                float coor_y = cur.getFloat(5);
                Time time = cur.getTime(6);

                int battery = cur.getInt(7);
                int speed = cur.getInt(8);

                boolean isShareLocation = cur.getInt(9) == 1 ? true : false;
                boolean isShareBaterry = cur.getInt(10) == 1 ? true : false;
                boolean isShareSpeed = cur.getInt(11) == 1 ? true : false;

                Member member = new Member(userName, new Dynamic_MyLocation(coor_x, coor_y, time), battery, speed, isShareLocation, isShareBaterry, isShareSpeed);
                list_member.add(member);
            }

            Log.e("Circle17", "Query success!");
            return list_member;
        } catch (SQLException e) {
            Log.e("Circle17", "Query fail");
            e.printStackTrace();
            return null;
        }
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
}
