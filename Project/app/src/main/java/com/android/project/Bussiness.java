package com.android.project;

import android.util.Log;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    public static boolean register(String userName, String passWord) {
        try {
            //truy xuất cơ sở dữ liệu sql
            Statement statement = MainActivity.connection.createStatement();
            //Chọn ID cho tài khoản mới
            ResultSet resultSet = statement.executeQuery("Select MAX(ID_Account) from Account");
            resultSet.next();
            int newID = resultSet.getInt(1) + 1;
            //Đưa dữ liệu lên Database
            statement.executeUpdate("Insert into Account (ID_Account, UserName, PassWord, Coordinates_X, Coordinates_Y, RealTime, Battery, Speed, Share_Location, Share_Battery, Share_Speed) "
                    + " values (" + newID + ", '" + userName + "', '" + passWord + "', 12.2, 106.78, '2019-11-17 23:00:00', 100, 120, 1 , 1, 1)");
            // resultSet.next();
            Log.e("Circle17", "Insert success!");
            return true;
        } catch (SQLException e) {
            Log.e("Circle17", "Insert fail");
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