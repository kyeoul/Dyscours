package com.example.dyscours;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements fragmentSpectate.OnFragmentInteractionListener, fragmentParticipate.OnFragmentInteractionListener {
    public static final String TAG = "TagMainActivity";
    private FirebaseHelper firebaseHelper;
    private DyscoursFragment currentFragment;
    private ArrayList<Debate> participateDebates;
    private ArrayList<Debate> spectateDebates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        firebaseHelper = new FirebaseHelper();
        firebaseHelper.getAllDebates(this);

        if (savedInstanceState == null) {
            Log.d(TAG, "savedInst");
            currentFragment = new fragmentParticipate();
            loadFragment();
        }

        startActivity(new Intent(this, ChatActivity.class));
    }

    public boolean loadFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).addToBackStack(null).commit();
        return false;
    }

    public void onFragmentInteraction(Uri uri){

    }

    public void reloadDebates(){
        currentFragment.updateView();
        participateDebates.clear();
        spectateDebates.clear();
        firebaseHelper.getAllDebates(this);
    }

    public void addDebate(Debate debate){
        Log.d(TAG, "addDebate");
        if (debate.isOpenForParticipate()){
            participateDebates.add(debate);
        }
        else {
            spectateDebates.add(debate);
        }
    }

    // TODO: FIx this
    public void removeDebate(Debate debate){
        int index = findDebate(spectateDebates, debate.getKey(), 0, spectateDebates.size());
        spectateDebates.remove(index);
    }

    private int findDebate(ArrayList<Debate> debates, String key, int start, int end){
        if (start > end || start > debates.size()){
            return -1;
        }
        if (start == end){
            return start;
        }
        int check = (start + end)/2;
        int compare = debates.get(check).getKey().compareTo(key);
        if (compare < 0){
            return findDebate(debates, key, check + 1, end);
        }
        else if (compare > 0){
            return findDebate(debates, key, start, check);
        }
        else {
            return check;
        }
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
}
