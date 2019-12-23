package com.android.project.ModelDatabase;

public class JoinModel {
    private String circlename;
    private String admin;
    private MessageModel lastMessage = null;

    public JoinModel(){

    }

    public JoinModel(String circlename, String admin){
        this.circlename = circlename;
        this.admin = admin;
    }

    public JoinModel(String circlename, String admin, MessageModel lastMessage){
        this.circlename = circlename;
        this.admin = admin;
        this.lastMessage = lastMessage;
    }

    public void setLastMessage(MessageModel lastMessage){
        this.lastMessage = lastMessage;
    }

    public String getCirclename(){
        return circlename;
    }

    public String getAdmin(){
        return admin;
    }

    public MessageModel getLastMessage(){
        return lastMessage;
    }
}
