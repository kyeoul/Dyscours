package com.example.dyscours;

public class Debate {

    private String debateName;
    private String userName;

    public Debate(String debateName, String userName){
        this.debateName = debateName;
        this.userName = userName;
    }

    public String getDebateName() {
        return debateName;
    }

    public void setDebateName(String debateName) {
        this.debateName = debateName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
