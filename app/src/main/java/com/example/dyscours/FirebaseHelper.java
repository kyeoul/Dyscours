package com.example.dyscours;

import android.media.MediaPlayer;
import android.os.Handler;
import android.provider.ContactsContract;
import android.renderscript.Sampler;
import android.util.Log;
import android.widget.Toast;

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

/**
 * This is the most important class in the project. It allows easy access to the Firebase database, and other important information.
 */
public class FirebaseHelper {
    private static DatabaseReference mFirebaseDatabaseReference;
    private static final String TAG = "TagFirebaseHelper";
    private static Debate currentDebate; // The current debate in user
    private static boolean runTimerAllowed; // Whether running the timer is allowed
    private static MainActivity currentMainActivity; // The current instance of the main activity
    private static ChildEventListener currentChildEventListener; // The current child event listener in the debates section of the Firebase database
    private static FirebaseHelper currentInstance; // The current instance of FirebaseHelper
    private static Settings currentSettings; // The current instance of the settings object

    /**
     * Constructor: Please do not call this, and instead use the getInstance() function
     */
    public FirebaseHelper() {
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        currentDebate = null;
        currentChildEventListener = null;
        runTimerAllowed = true;
        updateSettings();
    }

    /**
     * Gets the instance of the FirebaseHelper
     * @return An instance of FirebaseHelper
     */
    public static FirebaseHelper getInstance(){
        Log.d(TAG, "newInstance");
        if (currentInstance  == null){
            currentInstance = new FirebaseHelper();
        }
        return currentInstance;
    }

    /**
     * Starts a debate with given information
     * @param debate A debate object: the only fields necessary are the debateName and the timeLimit
     * @param chatActivity A ChatActivity which will be used to start the debate
     * @return Whether the debate was successfully started
     */
    public boolean startDebate(final Debate debate, final ChatActivity chatActivity){
        // Allow the timer to run
        runTimerAllowed = true;
        // Cancel the debate commencement if certain values are not proivided
        if (debate.getTimeLimit() < 0 || debate.getDebateName() == null){
            return false;
        }
        // Set various parameters of the debate to ensure that they are correctly input into the database
        debate.setUser1(true);
        debate.setHasUser2Joined(false);
        debate.setClosed(false);
        debate.setOpenForParticipate(true);
        debate.setUserId(getUserId());
        // Create the debate in the database
        final DatabaseReference db = mFirebaseDatabaseReference.child("debates");
        // Get debate key, reserving a spot in the Firebase database
        final String key =  db.push().getKey();
        debate.setKey(key);
        currentDebate = debate;
        final FirebaseHelper finalThis = this;
        // Set the user id in the Firebase database: this must be done first for security reasons
        mFirebaseDatabaseReference.child("debateIds").child(key).child("user1").setValue(getUserId(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                Log.d(TAG, "onCompleteAdd");
                DatabaseReference dbX = mFirebaseDatabaseReference.child("users").child(finalThis.getUserId());
                // Get the score (userRating) of the user
                dbX.child("score").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int userRating = 0;
                        if (dataSnapshot.getValue() != null) {
                            userRating = (((Long) dataSnapshot.getValue()).intValue());
                        }
                        Log.d(TAG, "onDataChange" + userRating);
                        // create the final data package that will be sent to Firebase to start the debate
                        debate.setUser1Rating(userRating);
                        Map<String, Object> newData = new HashMap<>();
                        newData.put("debateName", debate.getDebateName());
                        newData.put("timeLimit", debate.getTimeLimit());
                        newData.put("user1Rating", debate.getUser1Rating());
                        newData.put("isOpenForParticipate", debate.isOpenForParticipate());
                        newData.put("debateRatingUser1", -1);
                        newData.put("debateRatingUser2", -1);
                        newData.put("isClosed", debate.isClosed());
                        newData.put("hasUser2Joined", debate.isHasUser2Joined());
                        db.child(key).setValue(newData);
                        // Listen for when the debate will be closed.
                        finalThis.listenForClosedDebate(chatActivity);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
        // Set the timer to read the initial time limit until the debate starts
        chatActivity.updateTimer(debate.getTimeLimit());
        // Listen for messages in the debate
        db.child(key).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "onChildAddedA");
                Message message = dataSnapshot.getValue(Message.class);
                // when a messages is received, push it to the chat. Note: this is also how a user's own messages are displayed.
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
        // Synchronize the Time in the database
        final DatabaseReference timeDb = mFirebaseDatabaseReference.child("time");
        // When the second user has joined, begin the process of timing
        db.child(key).child("hasUser2Joined").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null && ((Boolean)dataSnapshot.getValue()).booleanValue()) {
                    // The timeStart time stamp is added by the second user, and can be read off by the first users
                    db.child(key).child("timeStart").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            debate.setTimeStart(((Long) dataSnapshot.getValue()).longValue());
                            // Now we determine the time offset to ensure that that clocks are as synchronized as possible. This requires another push to the database
                            final String tempKey = timeDb.push().getKey();
                            timeDb.child(tempKey).setValue(ServerValue.TIMESTAMP, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    timeDb.child(tempKey).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() != null) {
                                                // Having determined the current time and the time the debate starts, we calculate the offset and adjust as appropriate.
                                                long nowTime = ((Long) dataSnapshot.getValue()).longValue();
                                                int offset = (int) ((nowTime - debate.getTimeStart()) / 1000.0);
                                                Log.d(TAG, offset + "");
                                                // starts the (local) clock on the debate
                                                finalThis.startClock(debate.getTimeLimit() - offset, chatActivity);
                                                Toast.makeText(chatActivity, "Someone has joined your debate. Start talking!", Toast.LENGTH_SHORT).show();
                                                timeDb.child(tempKey).setValue(null);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            });
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        return true;
    }

    /**
     * Listens for applause, which is generated only by spectators
     * @param chatActivity The chat activity which is being used currently.
     */
    public void addApplauseListener(final ChatActivity chatActivity){
        // There mus be a current debate
        if (currentDebate == null){
            return;
        }
        // Listens for applause events from spectators. Note: This is also how a user's own applause is heard on their device
        mFirebaseDatabaseReference.child("debates").child(currentDebate.getKey()).child("applause").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                chatActivity.applaud();
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
    }

    /**
     * Applauds in the current debate by pushing an event to the Firebase database. Note: this method does NOT actually generate a sound.
     */
    public void applaud(){
        if (currentDebate == null){
            return;
        }
        mFirebaseDatabaseReference.child("debates").child(currentDebate.getKey()).child("applause").push().setValue(true);
    }

    /**
     * Joins a debate started by another user
     * @param debate A debate object. The only required information is the key of the debate. However, the debate must be open for participation.
     * @param chatActivity The chat activity being used to conduct the debate.
     * @return
     */
    public boolean joinDebate(final Debate debate, final ChatActivity chatActivity){
        // If there is no key, the debate cannot be joined
        if (debate.getKey() == null){
            return false;
        }
        // Housekeeping
        currentDebate = debate;
        runTimerAllowed = true;
        // Sets important information in the debate to ensure that everything works properly
        debate.setUser1(false);
        final DatabaseReference db = mFirebaseDatabaseReference.child("debates");
        final String key = debate.getKey();
        final FirebaseHelper finalThis = this;
        debate.setUserId(getUserId());
        // The userId is the first thing that must be updated for security reasons.
        mFirebaseDatabaseReference.child("debateIds").child(key).child("user2").setValue(getUserId(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                putUserRatingInDebate(); // puts this user's rating (score) into the debate information
                // Set important information in the debate
                db.child(key).child("isOpenForParticipate").setValue(false);
                db.child(key).child("timeStart").setValue(ServerValue.TIMESTAMP, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        startClock(debate.getTimeLimit(), chatActivity);
                    }
                });
                db.child(key).child("hasUser2Joined").setValue(true);
                // Listen for when the debate has closed
                finalThis.listenForClosedDebate(chatActivity);
            }
        });
        // synchronize local debate with updated information
        debate.setOpenForParticipate(false);
        // Gets information about the debate from the database
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
        // Listens for messages in this debate
        db.child(key).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message = dataSnapshot.getValue(Message.class);
                Log.d(TAG, message.getContent());
                // When a message is received, display it. Note: this is also how a user's own messages are displayed
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
     * Joins a debate for spectation. A user cannot send messages to the debate
     * @param debate The debate object to be used; the only required information is the key.
     * @param chatActivity The chat activity being used for the debate
     * @return Whether the debate was joined successfully
     */
    public boolean spectateDebate(final Debate debate, final ChatActivity chatActivity){
        // If no key is provided, the debate cannot be joined.
        if (debate.getKey() == null){
            return false;
        }
        // Sets basic information for the debate to be joined
        runTimerAllowed = true;
        debate.setUser1(false);
        final DatabaseReference db = mFirebaseDatabaseReference.child("debates");
        final String key = debate.getKey();
        currentDebate = debate;
        // Listen for the debate to be closed
        listenForClosedDebate(chatActivity);
        // Get basic information about this debate
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
        // Listen for messages in this debate
        db.child(key).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message = dataSnapshot.getValue(Message.class);
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
        final FirebaseHelper finalThis = this;
        // When the debate is joined by the second user, or if it has already been joined, the spectator's clock will need to be synchronized to the clocks of th two participants.
        final DatabaseReference timeDb = mFirebaseDatabaseReference.child("time");
        // Waits for debate to start
        db.child(key).child("hasUser2Joined").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // If the debate starts
                if (dataSnapshot.getValue() != null && ((Boolean)dataSnapshot.getValue()).booleanValue()) {
                    // Get the time at which the debate was started
                    db.child(key).child("timeStart").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            debate.setTimeStart(((Long) dataSnapshot.getValue()).longValue());
                            final String tempKey = timeDb.push().getKey();
                            // Gets the current time from the database
                            timeDb.child(tempKey).setValue(ServerValue.TIMESTAMP, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    timeDb.child(tempKey).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() != null) {
                                                // Once the current time is determined, we can determine the amount of time in the debate that has elapsed.
                                                long nowTime = ((Long) dataSnapshot.getValue()).longValue();
                                                int offset = (int) ((nowTime - debate.getTimeStart()) / 1000.0);
                                                Log.d(TAG, offset + "");
                                                Toast.makeText(chatActivity, "User 2 has joined. The debate is beginning!", Toast.LENGTH_SHORT).show();
                                                // start the clock
                                                finalThis.startClock(debate.getTimeLimit() - offset, chatActivity);
                                                timeDb.child(tempKey).setValue(null);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    db.child(key).removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return true;
    }

    /**
     * Listens for when a debate has closed. Note: this is NOT used to determine when a user's own debate activity has terminated
     * @param chatActivity The chat activity corresponding to the debate in progress.
     */
    public void listenForClosedDebate(final ChatActivity chatActivity){
        final DatabaseReference db = mFirebaseDatabaseReference;
        // Listens for when the debate has closed
        db.child("debates").child(currentDebate.getKey()).child("isClosed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // If the debate has closed
                if (dataSnapshot.getValue() != null && ((Boolean)dataSnapshot.getValue()).booleanValue()){
                    if (chatActivity != null && !chatActivity.isDestroyed()){
                        Toast.makeText(chatActivity, "The debate has ended prematurely.", Toast.LENGTH_SHORT).show();
                        // The finish method of the chatActivity contains all the machinery to end a debate locally
                        chatActivity.finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * After the debate is rated by the user and deleteDebate() is run, this method wraps things up on the local end.
     * @param key The key of the debate
     */
    public void closeDebate(String key){
        if (currentDebate != null && currentDebate.getKey().equals(key)) {
            DatabaseReference db = mFirebaseDatabaseReference.child("debates");
            currentDebate = null;
        }
        runTimerAllowed = false;
    }

    /**
     * After the debate is rated by the user, this method is called, wrapping things up on the database end
     * @param key The key of the debate
     */
    public void deleteDebate(final String key){
        final DatabaseReference db = mFirebaseDatabaseReference;
        db.child("debates").child(key).setValue(null, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                db.child("debateIds").child(key).setValue(null);
            }
        });
    }

    /**
     * Sends a message to the current debate
     * @param message The message to be sent
     * @return true
     */
    public boolean sendMessage(Message message){
        DatabaseReference db = mFirebaseDatabaseReference.child("debates").child(currentDebate.getKey());
        message.setUser(currentDebate.isUser1() ? 1 : 2);
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
        return currentDebate;
    }

    public void setCurrentdebate(Debate currentdebate) {
        this.currentDebate = currentdebate;
    }

    /**
     * Puts the current users userRating (score) in the current debate
     */
    public void putUserRatingInDebate(){
        DatabaseReference dbX = mFirebaseDatabaseReference.child("users").child(getUserId());
        // Gets the user's score
        dbX.child("score").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int userRating = 0;
                if (dataSnapshot.getValue() != null) {
                    userRating = (((Long) dataSnapshot.getValue()).intValue());
                }
                DatabaseReference nDb = mFirebaseDatabaseReference.child("debates").child(currentDebate.getKey());
                nDb.child("user" + (currentDebate.isUser1() ? 1 : 2) + "Rating").setValue(userRating);
                // puts the score in the appropriate debate field
                if (currentDebate.isUser1()){
                    currentDebate.setUser1Rating(userRating);
                }
                else {
                    currentDebate.setUser2Rating(userRating);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Once a debate has finished, allows a user to rate the debate
     * @param rating The rating, from 0 to 100, that the user gives the debate
     */
    public void rateDebate(final int rating){
        final FirebaseHelper finalThis = this;
        DatabaseReference db = mFirebaseDatabaseReference.child("debates").child(currentDebate.getKey());
        // Sets the user's own debateRating in the debate
        db.child("debateRatingUser" + (currentDebate.isUser1() ? 1 : 2)).setValue(rating);
        final Debate finalCurrentDebate = currentDebate;
        // Listens for when the other user has done the same.
        db.child("debateRatingUser" + (currentDebate.isUser1() ? 2 : 1)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){
                    return;
                }
                // In this context, user1 corresponds to the local user and user2 corresponds to the "other" user
                int user1Rating = rating;
                int user2Rating = ((Long) dataSnapshot.getValue()).intValue();
                // Negative ratings are unacceptable
                if (user2Rating < 0){
                    return;
                }
                final DatabaseReference dbN = mFirebaseDatabaseReference.child("users").child(finalCurrentDebate.getUserId());
                // The arithmetic mean of the two users' debateRatings is the final debate rating
                final int averageRating = (user1Rating + user2Rating)/2;
                // The only record kept of the debate is the debate key and the final debate rating coming from that
                dbN.child("ratingHistory").child(finalCurrentDebate.getKey()).setValue(averageRating);
                // Gets the current score of the user
                dbN.child("score").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int oldScore = 0;
                        if (dataSnapshot.getValue() != null){
                            oldScore = ((Long) dataSnapshot.getValue()).intValue();
                        }
                        // Adds the final debate rating to the user's score
                        dbN.child("score").setValue(oldScore + averageRating);
                        finalThis.deleteDebate(finalCurrentDebate.getKey());
                        finalThis.closeDebate(finalCurrentDebate.getKey());
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

    /**
     * Starts a listener that populates the home page so that users can see what debates they can spectate and join
     * @param mainActivity The main activity to be populated with debates
     * @return Whether the listener was successfully initialized
     */
    public boolean initDebateListener(final MainActivity mainActivity){
        /**
         * The main activity must be functional and open
         */
        if (mainActivity.isDestroyed()) {
            return false;
        }
        currentMainActivity = mainActivity;
        DatabaseReference db = mFirebaseDatabaseReference.child("debates");
        // Ensures that the listener for new debates is updated
        if (currentChildEventListener != null) {
            Log.d(TAG, "removedChildEventListener");
            db.removeEventListener(currentChildEventListener);
        }
        // Listens for debates
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    // Pulls debate information from the database and adds debates to the home page, sorted by new
                    Log.d(TAG, "onChildAdded");
                    Log.d(TAG, this.toString());
                    String key = dataSnapshot.getKey();
                    int user1Rating = ((Long) dataSnapshot.child("user1Rating").getValue()).intValue();
                    Log.d(TAG, "Rating" + dataSnapshot.child("user1Rating").getValue());
                    int timeLimit = ((Long) dataSnapshot.child("timeLimit").getValue()).intValue();
                    String debateName = (String) dataSnapshot.child("debateName").getValue();
                    boolean isOpenForParticipate = ((Boolean) dataSnapshot.child("isOpenForParticipate").getValue()).booleanValue();
                    boolean isClosed = ((Boolean) dataSnapshot.child("isClosed").getValue()).booleanValue();
                    mainActivity.addDebate(new Debate(key, debateName, user1Rating, timeLimit, isOpenForParticipate, isClosed));
                    Log.d(TAG, "onChildAddedf");
                }
                catch (Exception e){
                    e.fillInStackTrace();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "childChanged");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "childRemoved");
                mainActivity.removeDebate(new Debate(dataSnapshot.getKey()));
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d(TAG, "childMoved");
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

    /**
     * Gets the users' settings from the database and updates them local settings to match that
     */
    public void updateSettings (){
        DatabaseReference db = mFirebaseDatabaseReference.child("users").child(getUserId()).child("settings");
        final FirebaseHelper finalThis = this;
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){
                    setSettings(new Settings());
                    finalThis.setCurrentSettings(new Settings());
                    return;
                }
                boolean isColorRed = ((Boolean) dataSnapshot.child("isColorRed").getValue()).booleanValue();
                boolean isApplauseOn = ((Boolean) dataSnapshot.child("isApplauseOn").getValue()).booleanValue();
                int applauseSound = ((Long) dataSnapshot.child("applauseSound").getValue()).intValue();
                finalThis.setCurrentSettings(new Settings(isColorRed, isApplauseOn, applauseSound));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Sets the database user's settings to match the local settings
     * @param settings
     */
    public void setSettings(Settings settings){
        DatabaseReference db = mFirebaseDatabaseReference.child("users").child(getUserId()).child("settings");
        Map<String, Object> newData = new HashMap<>();
        newData.put("isColorRed", settings.isColorRed());
        newData.put("isApplauseOn", settings.isApplauseOn());
        newData.put("applauseSound", settings.getApplauseSound());
        db.setValue(newData, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                updateSettings();
            }
        });
    }

    /**
     * Gets the local settings
     * @return The local settings
     */
    public Settings getSettings(){
        return getCurrentSettings();
    }

    /**
     * Starts the debate clock
     * @param timeLimit The time limit of the debate
     * @param chatActivity The chat activity corresponding to the active debate
     */
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

    /**
     * To be run when a user signs up
     * @param uid The user id of the user
     */
    public void initUser(String uid){
        // Sets the user's initial score to 0
        mFirebaseDatabaseReference.child("users").child(uid).child("score").setValue(0);
    }

    /**
     * This method starts a listener that gets important information about a user's account for the UserAccountActivity
     * @param userAccountActivity the userAccountActivity where the information will be displayed
     */
    public void startUserAccountListener(final UserAccountActivity userAccountActivity){
        // Gets the user's score
        mFirebaseDatabaseReference.child("users").child(getUserId()).child("score").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null){
                    return;
                }
                userAccountActivity.setUserScore(((Long) dataSnapshot.getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String getUserId(){
        return FirebaseAuth.getInstance().getUid();
    }

    public String getUserEmail(){
        return FirebaseAuth.getInstance().getCurrentUser().getEmail();
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

    public static Settings getCurrentSettings() {
        return currentSettings;
    }

    public static void setCurrentSettings(Settings currentSettings) {
        FirebaseHelper.currentSettings = currentSettings;
    }
}
