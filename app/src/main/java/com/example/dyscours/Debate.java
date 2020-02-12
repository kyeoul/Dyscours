package com.example.dyscours;

import android.os.Parcel;
import android.os.Parcelable;

public class Debate  {

    private String debateName;
    private String userId;
    private int timeLimit;
    private int key;

    public Debate(String debateName, String userId, int timeLimit){
        this.debateName = debateName;
        this.userId = userId;
        this.timeLimit = timeLimit;
    }

    public Debate(){
        this.debateName = "null";
        this.userId = "null";
        this.timeLimit = -1;
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

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }
}
