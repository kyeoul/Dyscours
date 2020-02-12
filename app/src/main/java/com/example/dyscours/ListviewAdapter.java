package com.example.dyscours;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ListviewAdapter extends BaseAdapter {

    private LayoutInflater inflater ;
    private ArrayList<Debate> debateList;

    public ListviewAdapter(Context applicationContext, ArrayList<Debate> debateList){
        this.debateList = debateList;
        inflater = LayoutInflater.from(applicationContext);
    }

    public int getCount(){
        return debateList.size();
    }

    public Object getItem(int i){return null;}

    public long getItemId(int i){return 0;}

    public View getView(int i, View view, ViewGroup viewGroup){
        view = inflater.inflate(R.layout.listview_layout, null);
        String debateName = debateList.get(i).getDebateName();
        String debateUser = "THIS MUST BE CHANGED LATER"; // CHANGE THIS LATER
        TextView debateNameView = (TextView) view.findViewById(R.id.debateName);
        debateNameView.setText(debateName);
        TextView debateUserView = (TextView) view.findViewById(R.id.debateUser);
        return view;
    }
}
