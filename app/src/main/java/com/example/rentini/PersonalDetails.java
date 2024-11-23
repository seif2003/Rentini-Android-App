package com.example.rentini;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rentini.databinding.ActivityPersonalDetailsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PersonalDetails extends AppCompatActivity {
    private ActivityPersonalDetailsBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPersonalDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setupUI();
        loadUserData();
    }

    private void setupUI() {
        // Back button click handler
        binding.backButton.setOnClickListener(v -> finish());
        
        // Save button click handler
        binding.saveButton.setOnClickListener(v -> saveUserDetails());
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // User not logged in, return to login screen
            finish();
            return;
        }

        // Load user data from Firestore
        db.collection("users").document(currentUser.getUid())
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // Populate fields with user data
                    binding.firstNameInput.setText(documentSnapshot.getString("firstName"));
                    binding.lastNameInput.setText(documentSnapshot.getString("lastName"));
                    binding.phoneInput.setText(documentSnapshot.getString("phone"));
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(PersonalDetails.this, 
                    "Error loading profile: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            });
    }

    private void saveUserDetails() {
        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        // Get input values
        String firstName = binding.firstNameInput.getText().toString().trim();
        String lastName = binding.lastNameInput.getText().toString().trim();
        String phone = binding.phoneInput.getText().toString().trim();

        // Validate inputs
        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create user data map
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("firstName", firstName);
        userUpdates.put("lastName", lastName);
        userUpdates.put("phone", phone);

        // Disable save button while updating
        binding.saveButton.setEnabled(false);

        // Update user profile in Firestore
        db.collection("users").document(currentUser.getUid())
            .update(userUpdates)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(PersonalDetails.this, 
                    "Profile updated successfully", 
                    Toast.LENGTH_SHORT).show();
                finish();
            })
            .addOnFailureListener(e -> {
                binding.saveButton.setEnabled(true);
                Toast.makeText(PersonalDetails.this, 
                    "Error updating profile: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}