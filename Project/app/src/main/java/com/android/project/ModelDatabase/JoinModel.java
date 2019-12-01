package com.android.project.ModelDatabase;

public class JoinModel {
    private String circleName;
    private String username;

    public JoinModel(){

    }

    public JoinModel(String circlename, String username){
        this.circleName = circlename;
        this.username = username;
    }

    public String getCirclename(){
        return circleName;
    }

    public String getUsername(){
        return username;
    }
}
