package com.example.dyscours;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * https://developer.android.com/guide/topics/ui/layout/recyclerview
 */
public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ChatViewHolder> {
    private ArrayList<Message> mDataset;
    private ChatActivity chatActivity;
    Context context;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ViewGroup viewGroup;
        public ChatViewHolder(ViewGroup v) {
            super(v);
            viewGroup = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ChatRecyclerAdapter(ArrayList<Message> myDataset, ChatActivity chatActivity) {
        mDataset = myDataset;
        this.chatActivity = chatActivity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ChatRecyclerAdapter.ChatViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_layout, parent, false);
        ChatViewHolder vh = new ChatViewHolder(v);
        context = parent.getContext();
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        TextView contentTextView = holder.viewGroup.findViewById(R.id.messageContentTextView);
        contentTextView.setText(mDataset.get(position).getContent());
        int user = chatActivity.getFirebaseHelper().getCurrentdebate().isUser1() ? 1 : 2;
        if (mDataset.get(position).getUser() == user) {
            contentTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) contentTextView.getLayoutParams();
            contentTextView.setBackground(chatActivity.getResources().getDrawable(R.drawable.that_chat_bubble));
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            contentTextView.setLayoutParams(layoutParams);
        }
        else {
            contentTextView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            contentTextView.setBackground(chatActivity.getResources().getDrawable(R.drawable.the_chat_bubble));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) contentTextView.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            contentTextView.setLayoutParams(layoutParams);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}