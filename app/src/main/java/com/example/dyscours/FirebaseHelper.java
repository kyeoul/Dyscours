package com.example.dyscours;

import android.renderscript.Sampler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import java.util.Map;

import java.time.Instant;

public class FirebaseHelper {
    private DatabaseReference mFirebaseDatabaseReference;
    private static final String TAG = "TagFirebaseHelper";
    private Debate currentdebate;
    private fragmentParticipate currentParticipate;
    private fragmentSpectate currentSpectate;
    private ChildEventListener currentChildEventListener;

    public FirebaseHelper() {
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        currentdebate = null;
        currentChildEventListener = null;

    }

    public boolean startDebate(Debate debate, final ChatActivity chatActivity){
        if (debate.getUserId() == null || debate.getTimeLimit() < 0 || debate.getDebateName() == null){
            return false;
        }
        DatabaseReference db = mFirebaseDatabaseReference.child("debates");
        String key =  db.push().getKey();
        Map<String, Object> newData = new HashMap<>();
        newData.put("debateName", debate.getDebateName());
        newData.put("user1", debate.getUserId());
        newData.put("timeLimit", debate.getTimeLimit());
        newData.put("user1Rating", debate.getUser1Rating());
        newData.put("isOpenForParticipate", true);
        newData.put("debateRatingUser1", -1);
        newData.put("debateRatingUser2", -1);
        db.child(key).setValue(newData);
        debate.setKey(key);
        debate.setUser1(true);
        currentdebate = debate;
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
        return true;
    }

    public boolean joinDebate(final Debate debate, final ChatActivity chatActivity){
        if (debate.getUserId() == null || debate.getKey() == null){
            return false;
        }
        DatabaseReference db = mFirebaseDatabaseReference.child("debates");
        String key = debate.getKey();
        db.child(key).child("user2").setValue(debate.getUserId());
        int user2Rating = debate.getUser2Rating();
        db.child(key).child("user2Rating").setValue(user2Rating);
        db.child(key).child("isOpenForParticipate").setValue(false);
        debate.setUser1(false);
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

    public void endDebate(){
        currentdebate = null;
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

    public int getUserRating(String userId){
        DatabaseReference db = mFirebaseDatabaseReference.child("users").child(userId);
         class A {
            private int m;
            A(int m){
                this.m = m;
            }

            public int get(){
                return m;
            }

            public void set(int m) {
                this.m = m;
            }
        }

        final A mInt = new A(-1);
        db.child("score").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mInt.set(((Long) dataSnapshot.child("rating").getValue()).intValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return mInt.get();
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
        if (mainActivity.isDestroyed())
            return false;
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

    public String getUserId(){
        return "TESTUID498476376";
    }
}
