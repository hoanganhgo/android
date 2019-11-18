package com.android.project;

import java.sql.Time;

public class Dynamic_Location extends Location {
    private Time realTime;

    public Time getRealTime(){return this.realTime;}
    public void setRealTime(Time realTime){this.realTime=realTime;}

    public Dynamic_Location(float coordinates_X, float coordinates_Y, Time realTime)
    {
        super(coordinates_X,coordinates_Y);
        this.realTime=realTime;
    }
}
