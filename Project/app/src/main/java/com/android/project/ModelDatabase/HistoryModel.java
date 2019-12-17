package com.android.project.ModelDatabase;

import java.util.Date;

public class HistoryModel {
    private String message;
    private String user;
    private long time;

    public HistoryModel(String user, String message) {
        this.message = message;
        this.user = user;

        // Initialize to current time
        time = new Date().getTime();
    }

    public HistoryModel(){

    }

    public String getMessage() {
        return message;
    }

    public String getUser() {
        return user;
    }

    public long getTime() {
        return time;
    }
}
