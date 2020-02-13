package com.example.dyscours;

import android.os.Parcel;
import android.os.Parcelable;

public class Debate  {

    private String debateName;
    private String userId;
    private boolean isUser1;
    private int postedTime;
    private int timeLimit;
    private String key;

    public Debate(String debateName, String userId, int timeLimit){
        this.debateName = debateName;
        this.userId = userId;
        this.timeLimit = timeLimit;
    }

    public Debate(){
        this.debateName = "null";
        this.userId = "null";
        this.timeLimit = -1;
        this.isUser1 = false;
    }


    public String getDebateName() {
        return debateName;
    }

    public void setDebateName(String debateName) {
        this.debateName = debateName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isUser1() {
        return isUser1;
    }

    public void setUser1(boolean user1) {
        isUser1 = user1;
    }
}
