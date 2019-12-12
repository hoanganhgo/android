package com.android.project.ClassObject;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MyLocation implements LocationListener {
    private float x;
    private float y;

    public MyLocation(float coordinates_x, float coordinates_y) {
        this.x=coordinates_x;
        this.y=coordinates_y;
    }

    public  MyLocation(){

    }


    public float getX(){return this.x;}
    public void setX(float coordinates_X){this.x=coordinates_X;}
    public float getY(){return this.y;}
    public void setY(float coordinates_Y){this.y=coordinates_Y;}

    @Override
    public void onLocationChanged(Location location) {
        this.x =(float) location.getLatitude();
        this.y =(float) location.getLongitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
