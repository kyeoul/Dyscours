package com.example.dyscours;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
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
        firebaseHelper = FirebaseHelper.getInstance();

        if (savedInstanceState == null) {
            Log.d(TAG, "savedInst");
            currentFragment = new fragmentParticipate();
            loadFragment();
        }


        getOverflowMenu();



    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadDebates();
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

    // TODO: FIx this
    public void removeDebate(Debate debate){
        int indexS = findDebate(spectateDebates, debate.getKey(), 0, spectateDebates.size());
        if (indexS >= 0) {
            if (currentFragment instanceof fragmentSpectate){
                spectateDebates.remove(indexS);
                currentFragment.updateViewRemoved(indexS);
            }
        }
        int indexP = findDebate(participateDebates, debate.getKey(), 0, participateDebates.size());
        if (indexP >= 0) {
            if (currentFragment instanceof fragmentParticipate){
                participateDebates.remove(indexP);
                currentFragment.updateViewRemoved(indexP);
            }
        }
    }

    private int findDebate(ArrayList<Debate> debates, String key, int start, int end){
        if (start > end || start >= debates.size()){
            return -1;
        }
        if (start == end){
            return start;
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

    //Menu Shenanigans

    //@Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_items, menu);
//        return true;
//    }

    public void onClickPopup(View v){showPopup(v, R.style.Widget_AppCompat_Light_PopupMenu);}

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
}
