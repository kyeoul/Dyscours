package com.example.dyscours;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Debate implements Serializable {

    private String debateName;
    private String userId;
    private boolean isUser1;
    private int startedTime;
    private int timeLimit;
    private String key;
    private int user1Rating;
    private int user2Rating;
    private int debateRatingUser1;
    private int debateRatingUser2;
    private boolean isOpenForParticipate;
    private boolean isClosed;
    private boolean hasUser2Joined;
    private long timeStart;

    /**
     * starting a debate
     * @param debateName
     * @param timeLimit
     * @param user1Rating
     */
    public Debate(String debateName, int timeLimit){
        this.debateName = debateName;
        this.userId = null;
        this.timeLimit = timeLimit;
        this.isOpenForParticipate = true;
        debateRatingUser1 = -1;
        debateRatingUser2 = -1;
        isClosed = false;
    }

    /**
     * joining a debate
     * @param key
     * @param user2Rating
     */
    public Debate (String key){
        this.userId = null;
        this.key = key;
        this.isOpenForParticipate = false;
        debateRatingUser1 = -1;
        debateRatingUser2 = -1;
        isClosed = false;
    }

    /**
     * looking at a debate from the home screen
     * @param key
     * @param debateName
     * @param user1Rating
     * @param timeLimit
     * @param isOpenForParticipate
     */
    public Debate(String key, String debateName, int user1Rating, int timeLimit, boolean isOpenForParticipate, boolean isClosed){
        this.key = key;
        this.user1Rating = user1Rating;
        this.timeLimit = timeLimit;
        this.debateName = debateName;
        this.isOpenForParticipate = isOpenForParticipate;
        this.isClosed = isClosed;
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

    public int getStartedTime() {
        return startedTime;
    }

    public void setStartedTime(int startedTime) {
        this.startedTime = startedTime;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public boolean isHasUser2Joined() {
        return hasUser2Joined;
    }

    public void setHasUser2Joined(boolean hasUser2Joined) {
        this.hasUser2Joined = hasUser2Joined;
    }

    public String toString(){
        return getKey() + getDebateName() + getUserId();
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }
}
