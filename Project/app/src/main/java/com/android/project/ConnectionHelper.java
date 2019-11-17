package com.android.project;
import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class ConnectionHelper {
    private String IP;
    private String PORT;
    private String DB;
    private String DBUserName;
    private String DBPassWord;
    @SuppressLint("NewAPI")
    public Connection connectToServer()
    {
        //Thông tin kết nối Database
        IP = "37.59.55.185";
        PORT="3306";
        DB="rb5YHmRcsc";
        DBUserName="rb5YHmRcsc";
        DBPassWord="tZqSHg4ISv";

        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        java.sql.Connection connection=null;
        String connectionURL=null;
        try{
            //Kết nối đến cơ sở dữ liệu SQL
            Class.forName("com.mysql.jdbc.Driver");
            connectionURL="jdbc:mysql://"+ IP +":"+PORT+"/"+DB;
            connection=DriverManager.getConnection(connectionURL,DBUserName, DBPassWord);
        }catch (SQLException se){
            Log.e("error from SQL", Objects.requireNonNull(se.getMessage()));

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
