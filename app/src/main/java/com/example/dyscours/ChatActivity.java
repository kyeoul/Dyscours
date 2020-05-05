package com.example.dyscours;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Activity where all user chats take place.
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
    private Settings settings;

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
        // When creating a chat activity, you need to put extras in your intent
        Bundle intentExtras = getIntent().getExtras();
        Debate debate = (Debate) intentExtras.getSerializable(DEBATE_VALUE);
        isParticipate = intentExtras.getBoolean(IS_PARTICIPATE);
        boolean isUser1 = intentExtras.getBoolean(IS_USER_1);
        timeView = findViewById(R.id.timerTextView);
        // Dealing with Settings
        settings = firebaseHelper.getSettings();
        int applauseSoundResId = ApplauseSound.getResIdFromId(settings.getApplauseSound());
        mediaPlayer = settings.isApplauseOn() ? MediaPlayer.create(this, applauseSoundResId) : null;

        //Gets debate name and sets it to be the action bar title
        String debateName = debate.getDebateName();
        if(debateName.length() > 25){
            debateName = debateName.substring(0,25);
            debateName += "...";
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView title = toolbar.findViewById(R.id.titleText);
        title.setText(debateName);

        if (isParticipate && !isUser1){ // CASE: Joining a debate to participate
            Log.d(TAG, "chatJoin");
            firebaseHelper.joinDebate(debate, this);
        }
        if (isParticipate && isUser1){ // CASE: Creating a debate to participate
            Log.d(TAG, "chatStart");
            Log.d(TAG, debate.toString());
            firebaseHelper.startDebate(debate, this);
        }
        if (!isParticipate){ // CASE: Joining a debate to spectate
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
        // In all cases, spectators can applaud which will be sent to all privy parties
        firebaseHelper.addApplauseListener(this);
        // Messaging recycler view initialization
        messages = new ArrayList<Message>();
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new ChatRecyclerAdapter(messages, this);
        recyclerView.setAdapter(mAdapter);
        // Info button
        ImageButton button = (ImageButton) findViewById(R.id.infoButton);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dialogBuilder();
            }
        });

    }

    public void createDialogBox(){

    }

    /**
     * Adds a message to the local chat Recyclerview
     * @param message The message to be added to the chat
     */
    public void addMessage(Message message){
        messages.add(message);
        mAdapter.notifyItemInserted(messages.size() - 1);
    }

    /**
     * Sends a message from the current user to the current debate in Firebase, based on the content of the message edit text: On Click
     * @param v The view that is being clicked
     */
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

    /**
     * Plays the applause sound effect locally; called when a spectator privy to the chat (including the current user) clicks on the applause button.
     */
    public void applaud(){
        if (settings.isApplauseOn()){
            mediaPlayer.start();
        }
    }

    /**
     * Updates the local timer, displayed in minutes and seconds
     * @param seconds The number of seconds to show on the timer
     */
    public void updateTimer(int seconds){
        Log.d(TAG, "updateTimer");
        int minutes = seconds/60;
        seconds = seconds%60;
        String out = minutes + (seconds < 10 ? ":0" : ":") + seconds;
        timeView.setText(out);
    }

    /**
     * When the activity stops for any reason, this method is called to wrap up the debate.
     */
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

    /**
     * Builds the local debate information dialog
     */
    public void dialogBuilder(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        final View finalView = getLayoutInflater().inflate(R.layout.dialog_debate_info, null);
        alertDialogBuilder.setTitle("Debate Information").setView(finalView).setNegativeButton("Back", null)
                .setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
                );
        AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();

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
}
