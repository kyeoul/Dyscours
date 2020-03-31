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

    public void onHomeClick(View v){
        finish();
    }

    public void onStartClick(){
        finish();
        FirebaseHelper.getInstance().getCurrentMainActivity().setAddDebateOnResume(true);
    }

    @Override
    public void finish() {
        SeekBar seekBar = findViewById(R.id.seekBarRating);
        int rating = seekBar.getProgress();
        FirebaseHelper.getInstance().rateDebate(rating);
        super.finish();
    }
}
