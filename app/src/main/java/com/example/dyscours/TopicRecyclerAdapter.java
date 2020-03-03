package com.example.dyscours;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Recycler view implementation greatly inspired by https://developer.android.com/guide/topics/ui/layout/recyclerview
 */

public class TopicRecyclerAdapter extends RecyclerView.Adapter<TopicRecyclerAdapter.MyViewHolder>{
    private ArrayList<Debate> debateArrayList;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ViewGroup viewGroup;
        public MyViewHolder(ViewGroup v){
            super(v);
            viewGroup = v;
        }
    }

    public TopicRecyclerAdapter(ArrayList<Debate> debateArrayList){
        this.debateArrayList = debateArrayList;
    }

    /**
     *
     * Onclick provided by https://stackoverflow.com/questions/24471109/recyclerview-onclick
     * @param parent
     * @param viewType
     * @return
     */
    public TopicRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_topic_layout, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(MyViewHolder holder, int position){
        TextView userText = holder.viewGroup.findViewById(R.id.debateName);
        TextView ratingText = holder.viewGroup.findViewById(R.id.debateUserRating);
        Debate debate = debateArrayList.get(position);
        String debateName = debate.getDebateName();
        int debateUserRating = debate.getUser1Rating();
        userText.setText(debateName);
        ratingText.setText(Integer.toString(debateUserRating));
    }

    public int getItemCount() { return debateArrayList.size(); }
}
