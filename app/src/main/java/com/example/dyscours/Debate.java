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
    private int user1Rating;
    private int user2Rating;
    private int debateRatingUser1;
    private int debateRatingUser2;
    private boolean isOpenForParticipate;


    public Debate(String debateName, String userId, int timeLimit, int user1Rating){
        this.debateName = debateName;
        this.userId = userId;
        this.timeLimit = timeLimit;
        this.isOpenForParticipate = true;
        debateRatingUser1 = -1;
        debateRatingUser2 = -1;
    }

    public Debate (String userId, String key, int user2Rating){
        this.userId = userId;
        this.key = key;
        this.isOpenForParticipate = false;
        debateRatingUser1 = -1;
        debateRatingUser2 = -1;
    }

    public Debate(String key, String debateName, int user1Rating, int timeLimit, boolean isOpenForParticipate){
        this.key = key;
        this.user1Rating = user1Rating;
        this.timeLimit = timeLimit;
        this.debateName = debateName;
        this.isOpenForParticipate = isOpenForParticipate;
        debateRatingUser1 = -1;
        debateRatingUser2 = -1;
    };


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

    public int getPostedTime() {
        return postedTime;
    }

    public void setPostedTime(int postedTime) {
        this.postedTime = postedTime;
    }

    public int getUser1Rating() {
        return user1Rating;
    }

    public void setUser1Rating(int user1Rating) {
        this.user1Rating = user1Rating;
    }

    public int getUser2Rating() {
        return user2Rating;
    }

    public void setUser2Rating(int user2Rating) {
        this.user2Rating = user2Rating;
    }

    public boolean isOpenForParticipate() {
        return isOpenForParticipate;
    }

    public void setOpenForParticipate(boolean openForParticipate) {
        isOpenForParticipate = openForParticipate;
    }

    public int getDebateRatingUser1() {
        return debateRatingUser1;
    }

    public void setDebateRatingUser1(int debateRatingUser1) {
        this.debateRatingUser1 = debateRatingUser1;
    }

    public int getDebateRatingUser2() {
        return debateRatingUser2;
    }

    public void setDebateRatingUser2(int debateRatingUser2) {
        this.debateRatingUser2 = debateRatingUser2;
    }
}
