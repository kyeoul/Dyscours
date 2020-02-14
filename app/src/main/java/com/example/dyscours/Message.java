package com.example.dyscours;

import java.util.HashMap;
import java.util.Map;

/**
 * Used https://config9.com/apps/firebase/when-making-a-pojo-in-firebase-can-you-use-servervalue-timestamp/
 */
public class Message {
    private String content;
    private int user;
    private Map<String, Object> timeStamp;

    public Message(){

    }

    public Message(String content){
        this.content = content;
    }

    public Message (String content, int user, Map<String, Object> timeStamp){
        this.content = content;
        this.user = user;
        this.timeStamp = timeStamp;
    }



    public String getContent() {
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

    public Map<String, Object> getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Map<String, Object> timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * https://stackoverflow.com/questions/16806903/convert-mapstring-object-to-mapstring-string
     * @return
     */
    public Map<String, String> getTimeStampAsMapStringstring() {
        Map<String,String> newMap = new HashMap<String,String>();
        for (Map.Entry<String, Object> entry : timeStamp.entrySet()) {
            if(entry.getValue() instanceof String){
                newMap.put(entry.getKey(), (String) entry.getValue());
            }
        }
        return newMap;
    }

    public void setTimeStampAsMapStringstring(Map<String, String> map){
        Map<String,Object> newMap = new HashMap<String,Object>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            newMap.put(entry.getKey(),  entry.getValue());

        }
        timeStamp = newMap;
    }
}
