package com.example.rentini;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentini.models.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messages;
    private Context context;
    private FirebaseAuth mAuth;

    public MessageAdapter(List<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.messageText.setText(message.getText());
        
        // Align messages based on sender
        boolean isCurrentUser = message.getUserId().equals(mAuth.getCurrentUser().getUid());
        holder.messageLayout.setGravity(isCurrentUser ? Gravity.END : Gravity.START);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        LinearLayout messageLayout;

        MessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            messageLayout = itemView.findViewById(R.id.messageLayout);
        }
    }
}