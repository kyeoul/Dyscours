package com.example.dyscours;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ArrayList<Debate> debateList = new ArrayList<>();
        debateList.add(new Debate("i discuss things", "jeneric"));
        debateList.add(new Debate("plz roastt me", "wfaieufhwoieufwhef"));
        debateList.add(new Debate("generic name", "generic username"));
        debateList.add(new Debate("generic name", "generic username"));
        debateList.add(new Debate("generic name", "generic username"));
        debateList.add(new Debate("generic name", "generic username"));
        debateList.add(new Debate("generic name", "generic username"));
        debateList.add(new Debate("generic name", "generic username"));
        debateList.add(new Debate("generic name", "generic username"));

        ListView listView = (ListView) findViewById(R.id.debateList);

        ListviewAdapter adapter = new ListviewAdapter(getApplicationContext(), debateList);
        listView.setAdapter(adapter);
    }
}
