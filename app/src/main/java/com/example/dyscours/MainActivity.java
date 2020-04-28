package com.example.dyscours;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.awt.font.NumericShaper;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.xml.datatype.Duration;

public class MainActivity extends AppCompatActivity implements fragmentSpectate.OnFragmentInteractionListener, fragmentParticipate.OnFragmentInteractionListener {
    public static final String TAG = "TagMainActivity";
    private FirebaseHelper firebaseHelper;
    private DyscoursFragment currentFragment;
    private ArrayList<Debate> participateDebates;
    private ArrayList<Debate> spectateDebates;
    private boolean addDebateOnResume;
    private int timeLimit;
    private AlertDialog currentDialog;
    public static final int MAX_TIME_LIMIT = 1439;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        timeLimit = 300;
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        BottomNavigationView navView = findViewById(R.id.navigationMain);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_spectate:
                        currentFragment = new fragmentSpectate();
                        loadFragment();
                        return true;
                    case R.id.nav_participate:
                        currentFragment = new fragmentParticipate();
                        loadFragment();
                        return true;
                }
                return false;
            }
        });

        participateDebates = new ArrayList<Debate>();
        spectateDebates = new ArrayList<Debate>();
        firebaseHelper = FirebaseHelper.getInstance();

        if (savedInstanceState == null) {
            Log.d(TAG, "savedInst");
            currentFragment = new fragmentParticipate();
            loadFragment();
        }

        addDebateOnResume = false;

        getOverflowMenu();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (addDebateOnResume) {
            dialogBuilder();
            addDebateOnResume = false;
        }
        Log.d(TAG, "onResume");
    }

    //exp
    private void getOverflowMenu() {

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //exp>

    public boolean loadFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).addToBackStack(null).commit();
        return false;
    }

    public void onFragmentInteraction(Uri uri){

    }

    public void reloadDebates(){
        participateDebates.clear();
        spectateDebates.clear();
        currentFragment.getTopicRecyclerAdapter().notifyDataSetChanged();
        firebaseHelper.initDebateListener(this);
    }

    public void addDebate(Debate debate){
        Log.d(TAG, "addDebate" + debate.getUser1Rating());
        if (firebaseHelper.getCurrentdebate() != null && debate.getKey().equals(firebaseHelper.getCurrentdebate().getKey())){
            return;
        }
        if (debate.isOpenForParticipate()){
            participateDebates.add(0,debate);
            if (currentFragment instanceof fragmentParticipate){
                currentFragment.updateViewAdded();
            }
        }
        if (!debate.isClosed()) {
            spectateDebates.add(0, debate);
            if (currentFragment instanceof fragmentSpectate){
                currentFragment.updateViewAdded();
            }
        }

    }

    public void removeDebate(Debate debate){
        int indexS = findDebate(spectateDebates, debate.getKey(), 0, spectateDebates.size());
        Log.d(TAG,"" + indexS);
        if (indexS >= 0 && indexS < spectateDebates.size()) {
            if (currentFragment instanceof fragmentSpectate){
                spectateDebates.remove(indexS);
                currentFragment.updateViewRemoved(indexS);
            }
        }
        int indexP = findDebate(participateDebates, debate.getKey(), 0, participateDebates.size());
        if (indexP >= 0 && indexP < participateDebates.size()) {
            if (currentFragment instanceof fragmentParticipate){
                participateDebates.remove(indexP);
                currentFragment.updateViewRemoved(indexP);
            }
        }
    }

    private int findDebate(ArrayList<Debate> debates, String key, int start, int end){
        if (start >= end || start >= debates.size() || end <= 0){
            return -1;
        }
        int check = (start + end)/2;
        int compare = debates.get(check).getKey().compareTo(key);
        if (compare > 0){
            return findDebate(debates, key, check + 1, end);
        }
        else if (compare < 0){
            return findDebate(debates, key, start, check);
        }
        else {
            return check;
        }
    }

    public void dialogBuilder(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final MainActivity finalThis = this;
        final View finalView = finalThis.getLayoutInflater().inflate(R.layout.dialog_add_debate, null);
        builder.setTitle("Creating a debate").setView(finalView).setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText opinionEditText = finalView.findViewById(R.id.opinionEditText);
                String debateName = opinionEditText.getText().toString();
                if (debateName == null || debateName.isEmpty()){
                    return;
                }
                int timeLimit = finalThis.getTimeLimit();
                if (timeLimit <= 0 || timeLimit > MAX_TIME_LIMIT){
                    return;
                }
                Debate debate = new Debate(opinionEditText.getText().toString(), timeLimit);
                Bundle bundle = new Bundle();
                bundle.putSerializable(ChatActivity.DEBATE_VALUE, debate);
                bundle.putBoolean(ChatActivity.IS_PARTICIPATE, true);
                bundle.putBoolean(ChatActivity.IS_USER_1, true);
                Intent intent = new Intent(finalThis, ChatActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        }).setNegativeButton("Exit", null);
        AlertDialog dialog = builder.create();
        dialog.show();
        currentDialog = dialog;
    }

    //Menu Shenanigans

    //@Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_items, menu);
//        return true;
//    }

    public void onClickPopup(View v){showPopup(v, R.style.Widget_AppCompat_Light_PopupMenu);}

   public void addDebateOnClick(View v){
        final MainActivity finalThis = this;
        DurationPicker durationPicker = new DurationPicker(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int minutes, int seconds) {
                finalThis.setTimeLimit(minutes, seconds);
            }
        }, 5, 0);
        durationPicker.show();
   }

   public void setTimeLimit(int minutes, int seconds){
        if (currentDialog != null && currentDialog.isShowing()) {
            timeLimit = 60 * minutes + seconds;
            EditText timeLimitEditText = currentDialog.findViewById(R.id.timeLimitEditText);
            String out = minutes + (seconds < 10 ? ":0" : ":") + seconds;
            timeLimitEditText.setText(out);
        }
   }

    public void showPopup(View v, int style){
        Context wrapper = new ContextThemeWrapper(this, style);

        PopupMenu popupMenu = new PopupMenu(wrapper, v);

        popupMenu.getMenuInflater().inflate(R.menu.menu_items, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return MainActivity.this.onOptionsItemSelected(menuItem);

            }
        });

        popupMenu.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return false;
    }

    public static String getTAG() {
        return TAG;
    }

    public FirebaseHelper getFirebaseHelper() {
        return firebaseHelper;
    }

    public void setFirebaseHelper(FirebaseHelper firebaseHelper) {
        this.firebaseHelper = firebaseHelper;
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    public void setCurrentFragment(DyscoursFragment currentFragment) {
        this.currentFragment = currentFragment;
    }

    public ArrayList<Debate> getParticipateDebates() {
        return participateDebates;
    }

    public void setParticipateDebates(ArrayList<Debate> participateDebates) {
        this.participateDebates = participateDebates;
    }

    public ArrayList<Debate> getSpectateDebates() {
        return spectateDebates;
    }

    public void setSpectateDebates(ArrayList<Debate> spectateDebates) {
        this.spectateDebates = spectateDebates;
    }

    public boolean isAddDebateOnResume() {
        return addDebateOnResume;
    }

    public void setAddDebateOnResume(boolean addDebateOnResume) {
        this.addDebateOnResume = addDebateOnResume;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }
}
