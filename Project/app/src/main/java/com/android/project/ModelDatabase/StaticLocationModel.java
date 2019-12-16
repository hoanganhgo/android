package com.android.project.ModelDatabase;

import java.util.UUID;

public class StaticLocationModel {
    private String guid;
    private double coor_x;
    private double coor_y;
    private String name;
    private String address;

    public StaticLocationModel() {
    }

    public StaticLocationModel(String name, String address, double coor_x, double coor_y) {
        this.coor_x = coor_x;
        this.coor_y = coor_y;
        this.name = name;
        this.address = address;
        this.guid = UUID.randomUUID().toString();
    }

    public StaticLocationModel(String guid, String name, String address, double coor_x, double coor_y) {
        this.coor_x = coor_x;
        this.coor_y = coor_y;
        this.name = name;
        this.address = address;
        this.guid = guid;
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

}
