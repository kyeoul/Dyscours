package com.example.dyscours;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Recycler view implementation greatly inspired by https://developer.android.com/guide/topics/ui/layout/recyclerview
 */
public class ChatActivity extends AppCompatActivity {
    public static final String TAG = "DyscoursDebugTagChat";
    private FirebaseHelper firebaseHelper;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarChat);
        setSupportActionBar(toolbar);

        firebaseHelper = new FirebaseHelper();
        firebaseHelper.startDebate(new Debate("We need more guns.", "TESTUSERID35", 5667, 200), this);

        messages = new ArrayList<Message>();

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ChatRecyclerAdapter(messages, this);
        recyclerView.setAdapter(mAdapter);
    }

    public void addMessage(Message message){
        messages.add(message);
        mAdapter.notifyDataSetChanged();
    }

    public void sendMessage(View v){
        EditText contentField = findViewById(R.id.messageEditText);
        String content = contentField.getText().toString();
        if(content.equals("")){
            Toast.makeText(this, "You need to type something", Toast.LENGTH_SHORT).show();
        }
        else {
            firebaseHelper.sendMessage(new Message(content));
            contentField.setText("");
            recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
        }
    }

    public FirebaseHelper getFirebaseHelper() {
        return firebaseHelper;
    }

    public void setFirebaseHelper(FirebaseHelper firebaseHelper) {
        this.firebaseHelper = firebaseHelper;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public RecyclerView.Adapter getmAdapter() {
        return mAdapter;
    }

    public void setmAdapter(RecyclerView.Adapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return layoutManager;
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    public void onLeaveClick(View v){
        Intent intent = new Intent(this, FinishedActivity.class);
        startActivity(intent);
    }
}
