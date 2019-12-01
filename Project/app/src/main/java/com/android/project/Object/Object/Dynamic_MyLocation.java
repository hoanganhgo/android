package com.android.project.Object;

import java.sql.Time;

public class Dynamic_MyLocation extends MyLocation {
    private Time realTime;

    public Time getRealTime(){return this.realTime;}
    public void setRealTime(Time realTime){this.realTime=realTime;}

    public Dynamic_MyLocation(float coordinates_X, float coordinates_Y, Time realTime)
    {
        super(coordinates_X,coordinates_Y);
        this.realTime=realTime;
    }
}
