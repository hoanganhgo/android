package com.android.project;

import java.sql.Time;

public class Static_MyLocation extends MyLocation {
    private Time checkIn;
    private Time checkOut;

    public Time getCheckIn(){return this.checkIn;}
    public void setCheckIn(Time checkIn){this.checkIn=checkIn;}
    public Time getCheckOut(){return this.checkOut;}
    public void setCheckOut(Time checkOut){this.checkOut=checkOut;}

    public Static_MyLocation(float coordinates_X, float coordinates_Y, Time checkIn, Time checkOut)
    {
        super(coordinates_X,coordinates_Y);
        this.checkIn=checkIn;
        this.checkOut=checkOut;
    }
}
