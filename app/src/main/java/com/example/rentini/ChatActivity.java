package com.example.rentini;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

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
    private String conversationId; // Add this field
    private ImageButton close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get conversation ID from intent
        conversationId = getIntent().getStringExtra("conversationId");
        if (conversationId == null) {
            // Generate new conversation ID if not provided
            conversationId = FirebaseDatabase.getInstance().getReference().push().getKey();
        }

        // Initialize Firebase with proper path
        messagesRef = FirebaseDatabase.getInstance().getReference()
            .child("conversations")
            .child(conversationId)
            .child("messages");

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
                    // Create message object
                    Message message = new Message(
                        messageText,
                        currentUser.getUid(),
                        System.currentTimeMillis()
                    );

                    // Generate unique key for message
                    String messageKey = messagesRef.push().getKey();
                    if (messageKey != null) {
                        // Save message using the generated key
                        messagesRef.child(messageKey)
                            .setValue(message)
                            .addOnSuccessListener(aVoid -> {
                                // Clear input after successful save
                                binding.messageInput.setText("");
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(ChatActivity.this, 
                                    "Failed to send message: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            });
                    }
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
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this,
                    "Failed to load messages: " + error.getMessage(),
                    Toast.LENGTH_SHORT).show();
            }
        });

        close = findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}