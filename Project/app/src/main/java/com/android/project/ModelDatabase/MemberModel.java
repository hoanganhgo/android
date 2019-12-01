package com.android.project.ModelDatabase;

public class MemberModel {
    private String UserName;
    private String PassWord;
    private int Battery;
    private int Speed;
    private float Coordinates_X;
    private float Coordinates_Y;
    //    private long RealTime;
    private boolean Share_Location;
    private boolean Share_Battery;
    private boolean Share_Speed;

    public MemberModel(){

    }

    public MemberModel(int battery, float coordinates_X, float coordinates_Y,
                       String passWord, boolean share_Battery,
                       boolean share_Location, boolean share_Speed, int speed, String UserName){
        this.Battery = battery;
        this.Coordinates_X = coordinates_X;
        this.Coordinates_Y = coordinates_Y;
        this.PassWord = passWord;
//        this.RealTime = realTime;
        this.Share_Battery = share_Battery;
        this.Share_Location = share_Location;
        this.Share_Speed = share_Speed;
        this.Speed = speed;
        this.UserName = UserName;
    }

    public String getUserName(){
        return UserName;
    }

    public int getBattery() {
        return Battery;
    }

    public void setBattery(int battery) {
        this.Battery = battery;
    }

    public int getSpeed() {
        return Speed;
    }

    public void setSpeed(int speed) {
        this.Speed = speed;
    }

    public boolean isShareLocation() {
        return Share_Location;
    }

    public void setShareLocation(boolean shareLocation) {
        this.Share_Location = shareLocation;
    }

    public boolean isShareBattery() {
        return Share_Battery;
    }

    public void setShareBattery(boolean shareBattery) {
        this.Share_Battery = shareBattery;
    }

    public boolean isShareSpeed() {
        return Share_Speed;
    }

    public void setShareSpeed(boolean shareSpeed) {
        this.Share_Speed = shareSpeed;
    }
}
