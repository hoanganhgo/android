package com.android.project;

import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Bussiness {

    //Nếu thông tin đăng nhập đúng sẽ trả về true
    public boolean login(String userName, String passWord, Connection connection)
    {
        try {
            //truy xuất cơ sở dữ liệu sql
            Statement statement=connection.createStatement();
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
}
