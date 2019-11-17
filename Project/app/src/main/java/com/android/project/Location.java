package com.android.project;

public class Location {
    private float x;
    private float y;

    public float getX(){return this.x;}
    public void setX(float coordinates_X){this.x=coordinates_X;}
    public float getY(){return this.y;}
    public void setY(float coordinates_Y){this.y=coordinates_Y;}

    public Location(float coordinates_X, float coordinates_Y)
    {
        this.x=coordinates_X;
        this.y=coordinates_Y;
    }
}
