package com.example.dyscours;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FirebaseHelper {
    private DatabaseReference mFirebaseDatabaseReference;
    private static final String TAG = "TagFirebaseHelper";
    public FirebaseHelper() {
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public String startDebate(Debate debate){
        DatabaseReference db = mFirebaseDatabaseReference.child("debates");
        String key =  db.push().getKey();
        Map<String, Object> newData = new HashMap<>();
        newData.put("debateName", debate.getDebateName());
        newData.put("user1", debate.getUserId());
        newData.put("timeLimit", debate.getTimeLimit());
        db.child(key).setValue(newData);
        return key; 
    }

    public Debate joinDebate(String key){
        DatabaseReference db = mFirebaseDatabaseReference.child("debates");
        return new Debate();
    }



}
