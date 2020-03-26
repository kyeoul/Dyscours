package com.example.dyscours;

import android.os.Handler;
import android.renderscript.Sampler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import java.time.Instant;

public class FirebaseHelper {
    private DatabaseReference mFirebaseDatabaseReference;
    private static final String TAG = "TagFirebaseHelper";
    private Debate currentdebate;
    private boolean runTimerAllowed;
    private MainActivity currentMainActivity;
    private ChildEventListener currentChildEventListener;
    private static FirebaseHelper currentInstance;

    public FirebaseHelper() {
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        currentdebate = null;
        currentChildEventListener = null;
        runTimerAllowed = true;
    }

    public static FirebaseHelper getInstance(){
        if (currentInstance  == null){
            currentInstance = new FirebaseHelper();
        }
        return currentInstance;
    }

    public boolean startDebate(final Debate debate, final ChatActivity chatActivity){
        runTimerAllowed = true;
        if (debate.getTimeLimit() < 0 || debate.getDebateName() == null){
            return false;
        }
        debate.setUser1(true);
        debate.setHasUser2Joined(false);
        debate.setClosed(false);
        debate.setOpenForParticipate(true);
        debate.setUserId(getUserId());
        final DatabaseReference db = mFirebaseDatabaseReference.child("debates");
        final String key =  db.push().getKey();
        debate.setKey(key);
        currentdebate = debate;
        final FirebaseHelper finalThis = this;
        mFirebaseDatabaseReference.child("debateIds").child(key).child("user1").setValue(getUserId(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                Log.d(TAG, "onCompleteAdd");
                finalThis.putUserRatingInDebate();
                Map<String, Object> newData = new HashMap<>();
                newData.put("debateName", debate.getDebateName());
                newData.put("user1", debate.getUserId());
                newData.put("timeLimit", debate.getTimeLimit());
                newData.put("user1Rating", debate.getUser1Rating());
                newData.put("isOpenForParticipate", debate.isOpenForParticipate());
                newData.put("debateRatingUser1", -1);
                newData.put("debateRatingUser2", -1);
                newData.put("isClosed", debate.isClosed());
                newData.put("hasUser2Joined", debate.isHasUser2Joined());
                db.child(key).setValue(newData);
                finalThis.listenForClosedDebate(chatActivity);
            }
        });
        chatActivity.updateTimer(debate.getTimeLimit());
        db.child(key).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildAddedA");
                Message message = dataSnapshot.getValue(Message.class);
                chatActivity.addMessage(message);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        db.child(key).child("hasUser2Joined").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null && ((Boolean)dataSnapshot.getValue()).booleanValue()) {
                    startClock(debate.getTimeLimit(), chatActivity);
                    db.child(key).removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return true;
    }

    public boolean joinDebate(final Debate debate, final ChatActivity chatActivity){
        if (debate.getKey() == null){
            return false;
        }
        currentdebate = debate;
        runTimerAllowed = true;
        debate.setUser1(false);
        final DatabaseReference db = mFirebaseDatabaseReference.child("debates");
        final String key = debate.getKey();
        final FirebaseHelper finalThis = this;
        debate.setUserId(getUserId());
        mFirebaseDatabaseReference.child("debateIds").child(key).child("user2").setValue(getUserId(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                putUserRatingInDebate();
                db.child(key).child("isOpenForParticipate").setValue(false);
                db.child(key).child("hasUser2Joined").setValue(true);
                finalThis.listenForClosedDebate(chatActivity);
            }
        });
        startClock(debate.getTimeLimit(), chatActivity);
        debate.setOpenForParticipate(false);
        db.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                debate.setDebateName((String) dataSnapshot.child("debateName").getValue());
                debate.setTimeLimit((int) ((long) dataSnapshot.child("timeLimit").getValue()));
                debate.setUser1Rating((int) ((long) dataSnapshot.child("user1Rating").getValue()));
                Log.d(TAG, debate.getDebateName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        db.child(key).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message = dataSnapshot.getValue(Message.class);
                Log.d(TAG, message.getContent());
                chatActivity.addMessage(message);
                Log.d(TAG, "onChildAddedB");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return true;
    }

    /**
     * TODO: implement this
     * @param debate
     * @param chatActivity
     * @return
     */
    public boolean spectateDebate(final Debate debate, final ChatActivity chatActivity){
        if (debate.getKey() == null){
            return false;
        }
        runTimerAllowed = true;
        debate.setUser1(false);
        DatabaseReference db = mFirebaseDatabaseReference.child("debates");
        String key = debate.getKey();
        currentdebate = debate;
        db.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                debate.setDebateName((String) dataSnapshot.child("debateName").getValue());
                debate.setTimeLimit((int) ((long) dataSnapshot.child("timeLimit").getValue()));
                debate.setUser1Rating((int) ((long) dataSnapshot.child("user1Rating").getValue()));
                Log.d(TAG, debate.getDebateName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        db.child(key).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message = dataSnapshot.getValue(Message.class);
                Log.d(TAG, message.getContent());
                chatActivity.addMessage(message);
                Log.d(TAG, "onChildAddedB");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return true;
    }

    public void listenForClosedDebate(final ChatActivity chatActivity){
        final DatabaseReference db = mFirebaseDatabaseReference;
        final FirebaseHelper finalThis = this;
        db.child("debates").child(currentdebate.getKey()).child("isClosed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null && ((Boolean)dataSnapshot.getValue()).booleanValue()){
                    if (chatActivity != null && !chatActivity.isFinishing() && !chatActivity.isDestroyed()){
                        chatActivity.finish();
                    }
                    db.child("debates").child(currentdebate.getKey()).setValue(null);
                    db.child("debateIds").child(currentdebate.getKey()).setValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void closeDebate(){
        DatabaseReference db = mFirebaseDatabaseReference.child("debates");
        db.child(currentdebate.getKey()).child("messages").removeEventListener(currentChildEventListener);
        currentChildEventListener = null;
        currentdebate = null;
        runTimerAllowed = false;
    }

    public boolean sendMessage(Message message){
        DatabaseReference db = mFirebaseDatabaseReference.child("debates").child(currentdebate.getKey());
        message.setUser(currentdebate.isUser1() ? 1 : 2);
        db.child("messages").push().setValue(message);
        return true;
    }

    public DatabaseReference getmFirebaseDatabaseReference() {
        return mFirebaseDatabaseReference;
    }

    public void setmFirebaseDatabaseReference(DatabaseReference mFirebaseDatabaseReference) {
        this.mFirebaseDatabaseReference = mFirebaseDatabaseReference;
    }

    public static String getTAG() {
        return TAG;
    }

    public Debate getCurrentdebate() {
        return currentdebate;
    }

    public void setCurrentdebate(Debate currentdebate) {
        this.currentdebate = currentdebate;
    }

    public void putUserRatingInDebate(){
        DatabaseReference db = mFirebaseDatabaseReference.child("users").child(getUserId());
        db.child("score").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int userRating = (((Long) dataSnapshot.getValue()).intValue());
                DatabaseReference nDb = mFirebaseDatabaseReference.child("debates").child(currentdebate.getKey());
                nDb.child("user" + (currentdebate.isUser1() ? 1 : 2) + "Rating").setValue(userRating);
                if (currentdebate.isUser1()){
                    currentdebate.setUser1Rating(userRating);
                }
                else {
                    currentdebate.setUser2Rating(userRating);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void rateDebate(final int rating){
        DatabaseReference db = mFirebaseDatabaseReference.child("debates").child(currentdebate.getKey());
        db.child("debateRatingUser" + (currentdebate.isUser1() ? 1 : 2)).setValue(rating);
        final Debate finalCurrentDebate = currentdebate;
        db.child("debateRatingUser" + (currentdebate.isUser1() ? 2 : 1)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int user1Rating = rating;
                int user2Rating = ((Long) dataSnapshot.getValue()).intValue();
                final DatabaseReference dbN = mFirebaseDatabaseReference.child("users").child(finalCurrentDebate.getUserId());
                final int averageRating = (user1Rating + user2Rating)/2;
                dbN.child("ratingHistory").child(finalCurrentDebate.getKey()).setValue(averageRating);
                dbN.child("score").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int oldScore = ((Long) dataSnapshot.getValue()).intValue();
                        dbN.child("score").setValue(oldScore + averageRating);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public boolean initDebateListener(final MainActivity mainActivity){
        if (mainActivity.isDestroyed()) {
            return false;
        }
        currentMainActivity = mainActivity;
        DatabaseReference db = mFirebaseDatabaseReference.child("debates");
        if (currentChildEventListener != null)
            db.removeEventListener(currentChildEventListener);
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildAdded");
                String key = dataSnapshot.getKey();
                int user1Rating = ((Long) dataSnapshot.child("user1Rating").getValue()).intValue();
                int timeLimit =  ((Long) dataSnapshot.child("timeLimit").getValue()).intValue();
                String debateName = (String) dataSnapshot.child("debateName").getValue();
                boolean isOpenForParticipate = ((Boolean) dataSnapshot.child("isOpenForParticipate").getValue()).booleanValue();
                mainActivity.addDebate(new Debate(key, debateName, user1Rating, timeLimit, isOpenForParticipate));
                Log.d(TAG, "onChildAddedf");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        db.addChildEventListener(childEventListener);
        currentChildEventListener = childEventListener;
        Log.d(TAG, "startMonitor3");
        return true;
    }

    public void startClock(final int timeLimit, final ChatActivity chatActivity){
        if (!runTimerAllowed)
            return;
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            private int secondsRemaining = timeLimit;
            @Override
            public void run() {
                if (!runTimerAllowed){
                    return;
                }
                Log.d(TAG, "clockRun");
                secondsRemaining--;
                if (secondsRemaining < 0){
                    chatActivity.finish();
                    return;
                }
                chatActivity.updateTimer(secondsRemaining);
                handler.postDelayed(this,1000);
            }
        };
        handler.post(runnable);
    }

    public void initUser(String uid){
        mFirebaseDatabaseReference.child("users").child(uid).child("score").setValue(0);
    }

    public String getUserId(){
        return FirebaseAuth.getInstance().getUid();
    }

    public boolean isRunTimerAllowed() {
        return runTimerAllowed;
    }

    public void setRunTimerAllowed(boolean runTimerAllowed) {
        this.runTimerAllowed = runTimerAllowed;
    }

    public MainActivity getCurrentMainActivity() {
        return currentMainActivity;
    }

    public void setCurrentMainActivity(MainActivity currentMainActivity) {
        this.currentMainActivity = currentMainActivity;
    }

    public ChildEventListener getCurrentChildEventListener() {
        return currentChildEventListener;
    }

    public void setCurrentChildEventListener(ChildEventListener currentChildEventListener) {
        this.currentChildEventListener = currentChildEventListener;
    }

    public static FirebaseHelper getCurrentInstance() {
        return currentInstance;
    }

    public static void setCurrentInstance(FirebaseHelper currentInstance) {
        FirebaseHelper.currentInstance = currentInstance;
    }
}
