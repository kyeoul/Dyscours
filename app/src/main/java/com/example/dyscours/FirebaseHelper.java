package com.example.dyscours;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {
    private DatabaseReference mFirebaseDatabaseReference;

    public FirebaseHelper() {
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("conversations");
    }

    public String startConversation(int timeLimit){
        String key =  mFirebaseDatabaseReference.push().getKey();
        mFirebaseDatabaseReference.child(key).setValue("BOOM");
        return key; 
    }



}
