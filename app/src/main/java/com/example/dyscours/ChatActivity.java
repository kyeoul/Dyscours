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
import android.widget.TextView;
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
    private TextView timeView;

    public static final int JOIN = 2;
    public static final int START = 1;
    public static final int SPECTATE = 3;

    public static final String DEBATE_VALUE = "debateValue";
    public static final String IS_PARTICIPATE = "isParticipate";
    public static final String IS_USER_1 = "isUser1";

    private boolean isParticipate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarChat);
        setSupportActionBar(toolbar);

        firebaseHelper = FirebaseHelper.getInstance();
        Bundle intentExtras = getIntent().getExtras();
        Debate debate = (Debate) intentExtras.getSerializable(DEBATE_VALUE);
        isParticipate = intentExtras.getBoolean(IS_PARTICIPATE);
        boolean isUser1 = intentExtras.getBoolean(IS_USER_1);

        if (isParticipate && !isUser1){
            Log.d(TAG, "chatJoin");
            firebaseHelper.joinDebate(debate, this);
        }
        if (isParticipate && isUser1){
            Log.d(TAG, "chatStart");
            Log.d(TAG, debate.toString());
            firebaseHelper.startDebate(debate, this);
        }
        if (!isParticipate){
            // TO DO: FINISH PARTICIPATE
            Log.d(TAG, "participateStart");
            findViewById(R.id.relativeLayout).setVisibility(View.INVISIBLE);
            firebaseHelper.spectateDebate(debate, this);
        }

        messages = new ArrayList<Message>();

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ChatRecyclerAdapter(messages, this);
        recyclerView.setAdapter(mAdapter);
        timeView = findViewById(R.id.timerTextView);

    }

    public void addMessage(Message message){
        messages.add(message);
        mAdapter.notifyItemInserted(messages.size() - 1);
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

    public void updateTimer(int seconds){
        int minutes = seconds/60;
        seconds = seconds%60;
        String out = minutes + ":" + (seconds < 10 ? " " : "") + seconds;
        timeView.setText(out);
    }

    public void onLeaveClick(View v){
        finish();
    }

    public void wrapUp(){
        if (isParticipate) {
            Intent intent = new Intent(this, FinishedActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        wrapUp();
        super.onStop();
    }
}
