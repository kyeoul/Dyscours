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
    private Fragment currentFragment;
    private ArrayList<Debate> participateDebates;
    private ArrayList<Debate> spectateDebates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            currentFragment = new fragmentParticipate();
            loadFragment();
        }

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

        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }

    public boolean loadFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).addToBackStack(null).commit();
        return false;
    }

    public void onFragmentInteraction(Uri uri){

    }

    public void addDebate(Debate debate){
        if (debate.isOpenForParticipate()){
            participateDebates.add(debate);
        }
        else if (debate.isOpenForParticipate()){
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
}
