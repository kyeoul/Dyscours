package com.example.dyscours;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

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

    private MediaPlayer mediaPlayer;

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
        timeView = findViewById(R.id.timerTextView);
        mediaPlayer = MediaPlayer.create(this, R.raw.clapping1);

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
            EditText chatText = findViewById(R.id.messageEditText);
            chatText.setFocusable(false);
            chatText.setHint("You're Spectating! Wait until the chat is over...");
            Button send = findViewById(R.id.messageButton);
            send.setVisibility(View.INVISIBLE);
            LinearLayout linearLayout = findViewById(R.id.linearLayout);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firebaseHelper.applaud();
                }
            });
            chatText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firebaseHelper.applaud();
                }
            });
            firebaseHelper.spectateDebate(debate, this);
        }
        firebaseHelper.addApplauseListener(this);
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
        mAdapter.notifyItemInserted(messages.size() - 1);
    }

    public void sendMessage(View v){
        EditText contentField = findViewById(R.id.messageEditText);
        String content = contentField.getText().toString().trim();
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

    public void applaud(){
        mediaPlayer.start();
    }

    public void updateTimer(int seconds){
        Log.d(TAG, "updateTimer");
        int minutes = seconds/60;
        seconds = seconds%60;
        String out = minutes + (seconds < 10 ? ":0" : ":") + seconds;
        timeView.setText(out);
    }

    public void onLeaveClick(View v){
        finish();
    }

    public void wrapUp(){
        if (isParticipate) {
            final String key = firebaseHelper.getCurrentdebate().getKey();
            DatabaseReference db = firebaseHelper.getmFirebaseDatabaseReference();
            db.child("debates").child(key).child("isClosed").setValue(true);
            final ChatActivity finalThis = this;
            ValueEventListener user2JoinedListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists() || !((Boolean) dataSnapshot.getValue()).booleanValue()){
                        firebaseHelper.deleteDebate(key);
                        firebaseHelper.closeDebate(key);
                        return;
                    }
                    Intent intent = new Intent(finalThis, FinishedActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            db.child("debates").child(key).child("hasUser2Joined").addListenerForSingleValueEvent(user2JoinedListener);

        }
    }

    @Override
    protected void onStop() {
        wrapUp();
        super.onStop();
    }
}
