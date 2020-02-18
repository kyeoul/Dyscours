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

import java.util.HashMap;
import java.util.Map;

import java.time.Instant;

public class FirebaseHelper {
    private DatabaseReference mFirebaseDatabaseReference;
    private static final String TAG = "TagFirebaseHelper";
    private Debate currentdebate;

    public FirebaseHelper() {
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        currentdebate = null;
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
        debate.setUser1(false);
        currentdebate = debate;
        db.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                debate.setDebateName((String) dataSnapshot.child("debateName").getValue());
                debate.setTimeLimit((int) ((long) dataSnapshot.child("timeLimit").getValue()));
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

    public void leaveDebate(){
        currentdebate = null;
    }

    public boolean sendMessage(Message message){
        DatabaseReference db = mFirebaseDatabaseReference.child("debates").child(currentdebate.getKey());
        message.setUser(currentdebate.isUser1() ? 1 : 2);
        db.child("messages").push().setValue(message);
        return true;
    }

}
