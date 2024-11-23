package com.example.rentini;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.rentini.databinding.ActivityChatBinding;
import com.example.rentini.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private DatabaseReference messagesRef;
    private List<Message> messages;
    private MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase
        messagesRef = FirebaseDatabase.getInstance().getReference("messages");

        // Initialize RecyclerView
        messages = new ArrayList<>();
        adapter = new MessageAdapter(messages, this);
        binding.messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.messagesRecyclerView.setAdapter(adapter);

        // Send message
        binding.sendButton.setOnClickListener(v -> {
            String messageText = binding.messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    Message message = new Message(
                        messageText, 
                        currentUser.getUid(), 
                        System.currentTimeMillis()
                    );
                    messagesRef.push().setValue(message);
                    binding.messageInput.setText("");
                }
            }
        });

        // Listen for messages
        messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                if (message != null) {
                    messages.add(message);
                    adapter.notifyItemInserted(messages.size() - 1);
                    binding.messagesRecyclerView.scrollToPosition(messages.size() - 1);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {}

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}