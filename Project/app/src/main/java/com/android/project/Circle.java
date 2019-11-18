package com.android.project;

import java.util.List;

public class Circle {
    private String circleName;
    private List<Member> members;
    private Member admin;
    private Static_Location locations;

    public Circle(String circleName, Member admin)
    {
        this.circleName = circleName;
        this.admin = admin;

    }

    public Circle(String circleName, List<Member> members, Member admin, Static_Location locations) {
        this.circleName = circleName;
        this.members = members;
        this.admin = admin;
        this.locations = locations;
    }

    public String getCircleName() {
        return circleName;
    }

    public void setCircleName(String circleName) {
        this.circleName = circleName;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public Member getAdmin() {
        return admin;
    }

    public void setAdmin(Member admin) {
        this.admin = admin;
    }

    public Static_Location getLocations() {
        return locations;
    }

    public void setLocations(Static_Location locations) {
        this.locations = locations;
    }
}
