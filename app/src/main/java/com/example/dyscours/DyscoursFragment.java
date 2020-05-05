package com.example.dyscours;


import android.widget.Adapter;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This abstract class exists to hold our Participate and Spectate fragments in one overarching class type
 */
public abstract class DyscoursFragment extends Fragment {
    /**
     * Updates the RecyclerView that holds debates on the home screen that a new debate has been added.
     */
    public abstract void updateViewAdded();
    /**
     * Updates the RecyclerView that holds debates on the home screen that a new debate has been removed.
     */
    public abstract void updateViewRemoved(int index);

    public abstract RecyclerView.Adapter getTopicRecyclerAdapter();

    /**
     * Reloads all debates in the main screen RecyclerView
     */
    public void reloadDebates(){
        ((MainActivity) getActivity()).reloadDebates();
    }
}
