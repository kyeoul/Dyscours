package com.example.dyscours;


import android.widget.Adapter;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public abstract class DyscoursFragment extends Fragment {
    public abstract void updateViewAdded();
    public abstract void updateViewRemoved(int index);
    public abstract RecyclerView.Adapter getTopicRecyclerAdapter();
    public void reloadDebates(){
        ((MainActivity) getActivity()).reloadDebates();
    }
}
