package com.android.project;

import android.util.Log;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Bussiness {

    //Nếu thông tin đăng nhập đúng sẽ trả về true
    public static boolean login(String userName, String passWord)
    {
        try {
            //truy xuất cơ sở dữ liệu sql
            Statement statement=MainActivity.connection.createStatement();
            ResultSet resultSet=statement.executeQuery("Select PassWord From Account Where UserName="+"'"+userName+"'");
            resultSet.next();
            //Lấy mật khẩu tương ứng với username
            String truePassWord=resultSet.getString(1);
            if (truePassWord.contentEquals(passWord))
            {
                return true;
            }
            else{
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean register(String userName, String passWord)
    {
        try {
            //truy xuất cơ sở dữ liệu sql
            Statement statement=MainActivity.connection.createStatement();
            //Chọn ID cho tài khoản mới
            ResultSet resultSet=statement.executeQuery("Select MAX(ID_Account) from Account");
            resultSet.next();
            int newID=resultSet.getInt(1)+1;
            //Đưa dữ liệu lên Database
            statement.executeUpdate("Insert into Account (ID_Account, UserName, PassWord, Coordinates_X, Coordinates_Y, RealTime, Battery, Speed, Share_Location, Share_Battery, Share_Speed) "
                    + " values ("+newID+", '"+userName+"', '"+passWord+"', 12.2, 106.78, '2019-11-17 23:00:00', 100, 120, 1 , 1, 1)");
           // resultSet.next();
            Log.e("hoanganh","Insert success!");
            return true;
        } catch (SQLException e) {
            Log.e("hoanganh","Insert fail");
            e.printStackTrace();
            return false;
        }
    }
}
