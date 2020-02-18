package com.example.dyscours;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ChatActivity extends AppCompatActivity {
    private FirebaseHelper firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        firebaseHelper = new FirebaseHelper();
        firebaseHelper.joinDebate(new Debate("TESTUSERID","-M0OSfMZJ49Vqo_aumsQ"), this);
    }

    public void addMessage(Message message){
        Toast.makeText(this, message.getContent(), Toast.LENGTH_SHORT);
    }

    public void sendMessage(View v){
        EditText contentField = findViewById(R.id.messageEditText);
        String content = contentField.getText().toString();
        firebaseHelper.sendMessage(new Message(content));
    }
}
