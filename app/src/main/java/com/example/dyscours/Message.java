package com.example.dyscours;

import java.util.HashMap;
import java.util.Map;

/**
 * This data structure is used to store information about a message
 */
public class Message {
    private String content; // The message body
    private int user; // The user who sent the message: 1 or 2

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
