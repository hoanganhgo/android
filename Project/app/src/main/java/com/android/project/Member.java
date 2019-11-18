package com.android.project;

import java.util.List;

public class Member {
    private String userName;
    private Dynamic_MyLocation location;
    private int battery;
    private int speed;
    private boolean shareLocation;
    private boolean shareBattery;
    private boolean shareSpeed;
    private List<Message> mailBox;

    public Member(String userName)
    {
        this.userName = userName;
    }

    public Member(String userName, Dynamic_MyLocation location, int battery, int speed, boolean shareLocation, boolean shareBattery, boolean shareSpeed) {
        this.userName = userName;
        this.location = location;
        this.battery = battery;
        this.speed = speed;
        this.shareLocation = shareLocation;
        this.shareBattery = shareBattery;
        this.shareSpeed = shareSpeed;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Dynamic_MyLocation getLocation() {
        return location;
    }

    public void setLocation(Dynamic_MyLocation location) {
        this.location = location;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public boolean isShareLocation() {
        return shareLocation;
    }

    public void setShareLocation(boolean shareLocation) {
        this.shareLocation = shareLocation;
    }

    public boolean isShareBattery() {
        return shareBattery;
    }

    public void setShareBattery(boolean shareBattery) {
        this.shareBattery = shareBattery;
    }

    public boolean isShareSpeed() {
        return shareSpeed;
    }

    public void setShareSpeed(boolean shareSpeed) {
        this.shareSpeed = shareSpeed;
    }

    public List<Message> getMailBox() {
        return mailBox;
    }

    public void setMailBox(List<Message> mailBox) {
        this.mailBox = mailBox;
    }

}
