package com.android.project.ModelDatabase;

import java.util.Date;

public class ConversationModel {
    private String circleName = "Circle Name";
    private String lastMessage = "Member: Last message!";


    public ConversationModel(String circleName, String lastMessage) {
        this.circleName = circleName;
        this.lastMessage = lastMessage;
    }

    public ConversationModel(){

    }

    public String getCircleNameConversation() {
        return circleName;
    }

    public void setCircleNameConversation(String circleName) {
        this.circleName = circleName;
    }

    public String getLastMessageConversation() {
        return lastMessage;
    }

    public void setLastMessageConversation(String lastMessage) {
        this.lastMessage = lastMessage;
    }

}
