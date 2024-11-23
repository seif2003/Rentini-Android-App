package com.example.rentini;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentini.models.Conversation;

import java.util.List;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ConversationViewHolder> {
    private List<Conversation> conversations;
    private Context context;
    private OnConversationClickListener listener;

    public interface OnConversationClickListener {
        void onConversationClick(Conversation conversation);
    }

    public ConversationsAdapter(List<Conversation> conversations, Context context, OnConversationClickListener listener) {
        this.conversations = conversations;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_conversation, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        Conversation conversation = conversations.get(position);
        holder.userNameText.setText(conversation.getOtherUserName());
        holder.lastMessageText.setText(conversation.getLastMessage());
        holder.itemView.setOnClickListener(v -> listener.onConversationClick(conversation));
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    static class ConversationViewHolder extends RecyclerView.ViewHolder {
        TextView userNameText;
        TextView lastMessageText;

        ConversationViewHolder(View itemView) {
            super(itemView);
            userNameText = itemView.findViewById(R.id.userNameText);
            lastMessageText = itemView.findViewById(R.id.lastMessageText);
        }
    }
}