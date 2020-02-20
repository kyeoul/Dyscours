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
}
