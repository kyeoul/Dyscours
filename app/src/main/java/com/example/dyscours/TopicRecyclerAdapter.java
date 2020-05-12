package com.example.dyscours;

import android.content.Intent;
import android.os.Bundle;
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
 * This recycler view is used to show debates in on the home page
 */

public class TopicRecyclerAdapter extends RecyclerView.Adapter<TopicRecyclerAdapter.MyViewHolder>{
    private ArrayList<Debate> debateArrayList;

    private MainActivity mainActivity;
    private DyscoursFragment fragment;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ViewGroup viewGroup;
        public MyViewHolder(ViewGroup v){
            super(v);
            viewGroup = v;
        }
    }

    public TopicRecyclerAdapter(ArrayList<Debate> debateArrayList, DyscoursFragment fragment, MainActivity mainActivity){
        this.debateArrayList = debateArrayList;
        this.fragment = fragment;
        this.mainActivity = mainActivity;
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

    /**
     * https://medium.com/@filswino/setting-onclicklistener-in-recyclerview-android-e6e198f5f0e2
     * @param holder
     * @param position
     */
    public void onBindViewHolder(MyViewHolder holder, int position){
        TextView userText = holder.viewGroup.findViewById(R.id.debateName);
        TextView ratingText = holder.viewGroup.findViewById(R.id.debateUserRating);

        final Debate debate = debateArrayList.get(position);
        String debateName = debate.getDebateName();
        int debateUserRating = debate.getUser1Rating();
        userText.setText(debateName);
        String string = "Rating: " + Integer.toString(debateUserRating);
        ratingText.setText(string);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseHelper firebaseHelper = mainActivity.getFirebaseHelper();
                // https://stackoverflow.com/questions/14333449/passing-data-through-intent-using-serializable
                Bundle bundle = new Bundle();
                bundle.putSerializable(ChatActivity.DEBATE_VALUE, debate);
                bundle.putBoolean(ChatActivity.IS_PARTICIPATE, fragment instanceof fragmentParticipate);
                bundle.putBoolean(ChatActivity.IS_USER_1, debate.isUser1());
                Intent intent = new Intent(mainActivity, ChatActivity.class);
                intent.putExtras(bundle);
                mainActivity.startActivity(intent);
            }
        });
    }

    public int getItemCount() { return debateArrayList.size(); }
}
