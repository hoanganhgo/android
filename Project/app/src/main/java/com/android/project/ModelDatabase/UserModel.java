package com.android.project.ModelDatabase;

public class UserModel {
    private String username;
    private String password;
    private int battery;
    private int speed;
    private float coor_x;
    private float coor_y;
    private long realtime;
    private int share_location;
    private int share_battery;
    private int share_speed;

    public UserModel(){

    }

    public UserModel(int battery, float coordinates_X, float coordinates_Y,
                     String passWord, int share_Battery, long realTime,
                     int share_Location, int share_Speed, int speed, String username){
        this.battery = battery;
        this.coor_x = coordinates_X;
        this.coor_y = coordinates_Y;
        this.password = passWord;
        this.realtime = realTime;
        this.share_battery = share_Battery;
        this.share_location = share_Location;
        this.share_speed = share_Speed;
        this.speed = speed;
        this.username = username;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){return password;}

    public int getBattery() {
        return battery;
    }
    public void setBattery(int battery) {
        this.battery = battery;
    }

    public float getCoor_x(){return coor_x;}
    public float getCoor_y(){return coor_y;}

    public int getSpeed() {
        return speed;
    }
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getShare_location() {
        return share_location;
    }
    public void setShareLocation(int shareLocation) {
        this.share_location = shareLocation;
    }

    public int getShare_battery() {
        return share_battery;
    }
    public void setShareBattery(int shareBattery) {
        this.share_battery = shareBattery;
    }

    public int getShare_speed() {
        return share_speed;
    }
    public void setShareSpeed(int shareSpeed) {
        this.share_speed = shareSpeed;
    }

    public long getRealtime(){return realtime;}
}