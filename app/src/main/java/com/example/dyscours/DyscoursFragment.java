package com.example.dyscours;


import androidx.fragment.app.Fragment;

public abstract class DyscoursFragment extends Fragment {
    public abstract void updateView();
    public abstract boolean isAllowTouch();
    public abstract void setAllowTouch(boolean allowTouch);
}
