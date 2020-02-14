package com.example.dyscours;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        FirebaseHelper firebaseHelper = new FirebaseHelper();
        firebaseHelper.startDebate(new Debate("We Need to Build a Wall.", "TESTUSERID", 500), this);
        firebaseHelper.sendMessage(new Message("Hi"));
    }

    public void addMessage(Message message){

    }
}
