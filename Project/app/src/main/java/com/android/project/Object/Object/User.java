package com.android.project.Object;

import java.util.List;

public class User extends Member{
    private List<Circle> circles;

    public User(String userName, List<Circle> circles, Dynamic_MyLocation location, int battery, int speed, boolean shareLocation, boolean shareBattery, boolean shareSpeed)
    {
        super(userName,location,battery,speed,shareLocation,shareBattery,shareSpeed);
        this.circles=circles;
    }
    public List<Circle> getCircles() {
        return circles;
    }

    public void setCircles(List<Circle> circles) {
        this.circles = circles;
    }
}
