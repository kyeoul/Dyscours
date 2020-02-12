package com.example.dyscours;

public class Debate {

    private String debateName;
    private String userId;
    private int startTime;
    private int endTime;

    public Debate(String debateName, String userId, int startTime, int endTime){
        this.debateName = debateName;
        this.userId = userId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Debate(){
        this.debateName = null;
        this.userId = null;
        this.startTime = -1;
        this.endTime = -2;
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

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }
}
