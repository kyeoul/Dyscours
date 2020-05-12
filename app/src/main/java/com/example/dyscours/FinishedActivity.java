package com.example.dyscours;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

/**
 * This is the activity that shows when a debate has ended.
 */
public class FinishedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finished);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button buttonStart = (Button) findViewById(R.id.startButton);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStartClick();
            }
        });
    }

    /**
     * On Click for the button that will return the user to the home menu
     * @param v
     */
    public void onHomeClick(View v){
        finish();
    }

    /**
     * On Click for the button that will return the user to the home page, and start a dialog to start another debate
     */
    public void onStartClick(){
        finish();
        FirebaseHelper.getInstance().getCurrentMainActivity().setAddDebateOnResume(true);
    }

    @Override
    /**
     * All roads lead to Rome: this is the method that ends the activity, and performs most of its functionality.
     */
    public void finish() {
        SeekBar seekBar = findViewById(R.id.seekBarRating);
        int rating = seekBar.getProgress();
        FirebaseHelper.getInstance().rateDebate(rating);
        super.finish();
    }
}
