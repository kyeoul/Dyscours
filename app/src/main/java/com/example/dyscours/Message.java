package com.example.dyscours;

import java.util.HashMap;
import java.util.Map;

/**
 * Used https://config9.com/apps/firebase/when-making-a-pojo-in-firebase-can-you-use-servervalue-timestamp/
 */
public class Message {
    private String content;
    private int user;

    public Message(){

    }

    public Message(String content){
        this.content = content;
    }

    public Message (String content, int user){
        this.content = content;
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public String toString(){
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }
}
