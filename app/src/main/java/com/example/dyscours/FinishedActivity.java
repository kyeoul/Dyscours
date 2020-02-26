package com.example.dyscours;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onStartClick(){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Creating a debate").setView(R.layout.dialog_add_debate).setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).setNegativeButton("Exit", null);
            builder.create().show();
    }
}
