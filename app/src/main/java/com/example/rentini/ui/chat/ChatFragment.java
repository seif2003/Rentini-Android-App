package com.example.rentini.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.rentini.ChatActivity;
import com.example.rentini.ConversationsAdapter;
import com.example.rentini.databinding.FragmentChatBinding;
import com.example.rentini.models.Conversation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment implements ConversationsAdapter.OnConversationClickListener {
    private FragmentChatBinding binding;
    private List<Conversation> conversations;
    private ConversationsAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ListenerRegistration conversationsListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, 
                            ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize RecyclerView
        conversations = new ArrayList<>();
        adapter = new ConversationsAdapter(conversations, requireContext(), this);
        binding.conversationsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.conversationsRecyclerView.setAdapter(adapter);

        loadConversations();

        return root;
    }

    private void loadConversations() {
        // Your existing loadConversations code
    }

    @Override
    public void onConversationClick(Conversation conversation) {
        // Handle conversation click by starting ChatActivity
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("conversationId", conversation.getId());
        intent.putExtra("otherUserId", conversation.getOtherUserId());
        intent.putExtra("otherUserName", conversation.getOtherUserName());
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (conversationsListener != null) {
            conversationsListener.remove();
        }
        binding = null;
    }
}