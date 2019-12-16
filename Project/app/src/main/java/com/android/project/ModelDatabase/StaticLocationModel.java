package com.android.project.ModelDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class StaticLocationModel {
    private String guid;
    private double coor_x;
    private double coor_y;
    private String name;
    private String address;
    private String checkin;
    private String checkout;

    public StaticLocationModel() {
    }

    public StaticLocationModel(String name, String address, double coor_x, double coor_y, String checkin, String checkout) {
        this.coor_x = coor_x;
        this.coor_y = coor_y;
        this.name = name;
        this.address = address;
        this.checkout = checkout;
        this.checkin = checkin;
        this.guid = UUID.randomUUID().toString();
    }

    public double getCoor_x() {
        return coor_x;
    }
    public void setCoor_x(double coor_x) {
        this.coor_x = coor_x;
    }

    public double getCoor_y() {
        return coor_y;
    }
    public void setCoor_y(double coor_y) {
        this.coor_y = coor_y;
    }

    public String getName(){
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getAddress(){
        return address;
    }

    public String getGuid(){
        return guid;
    }

    public String getCheckin(){
        return checkin;
    }
    public void setCheckin(String checkin){
        this.checkin = checkin;
    }

    public String getCheckout(){
        return checkout;
    }
    public void setCheckout(String checkout){
        this.checkout = checkout;
    }

    public static Calendar stringToCalendar(String string){
        Calendar calendar = Calendar.getInstance();

        String[] token = string.split(":");
        int hour = Integer.valueOf(token[0]);
        int minute = Integer.valueOf(token[1]);

        calendar.set(0,0,0,hour,minute,0);

        return calendar;
    }

    public static String calendarToString(Calendar calendar){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        return simpleDateFormat.format(calendar.getTime());
    }

}
