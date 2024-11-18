package com.example.rentini;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button b = findViewById(R.id.button2);
        Intent i = new Intent(this, SignIn.class);
        b.setOnClickListener(view -> startActivity(i));

        TextInputEditText firstName = findViewById(R.id.firstNameField);
        TextInputEditText lastName = findViewById(R.id.lastNameField);
        TextInputEditText email = findViewById(R.id.emailField);
        TextInputEditText phone = findViewById(R.id.phoneField);
        TextInputEditText password = findViewById(R.id.passwordField);
        TextInputEditText confPassword = findViewById(R.id.confirmPasswordField);

        Button login = findViewById(R.id.button);

        login.setOnClickListener(view -> {
            // Get all input values
            String firstNameText = firstName.getText().toString().trim();
            String lastNameText = lastName.getText().toString().trim();
            String emailText = email.getText().toString().trim();
            String phoneText = phone.getText().toString().trim();
            String passwordText = password.getText().toString().trim();
            String confPasswordText = confPassword.getText().toString().trim();

            // Validate inputs
            if (firstNameText.isEmpty()) {
                firstName.setError("First name is required");
                firstName.requestFocus();
                return;
            }

            if (lastNameText.isEmpty()) {
                lastName.setError("Last name is required");
                lastName.requestFocus();
                return;
            }

            if (emailText.isEmpty()) {
                email.setError("Email is required");
                email.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                email.setError("Please provide valid email");
                email.requestFocus();
                return;
            }

            if (phoneText.isEmpty()) {
                phone.setError("Phone number is required");
                phone.requestFocus();
                return;
            }

            if (passwordText.isEmpty()) {
                password.setError("Password is required");
                password.requestFocus();
                return;
            }

            if (passwordText.length() < 6) {
                password.setError("Password should be at least 6 characters");
                password.requestFocus();
                return;
            }

            if (!passwordText.equals(confPasswordText)) {
                confPassword.setError("Passwords don't match");
                confPassword.requestFocus();
                return;
            }

            // Show loading progress
            login.setEnabled(false);

            // Create user in Firebase Auth
            mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Store additional user details in Firestore
                            Map<String, Object> user = new HashMap<>();
                            user.put("firstName", firstNameText);
                            user.put("lastName", lastNameText);
                            user.put("email", emailText);
                            user.put("phone", phoneText);

                            db.collection("users")
                                    .document(mAuth.getCurrentUser().getUid())
                                    .set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(SignUp.this,
                                                "Registration successful!",
                                                Toast.LENGTH_LONG).show();
                                        // Navigate to main activity or login
                                        startActivity(new Intent(SignUp.this, SignIn.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(SignUp.this,
                                                "Failed to register user: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                        login.setEnabled(true);
                                    });
                        } else {
                            Toast.makeText(SignUp.this,
                                    "Failed to register: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                            login.setEnabled(true);
                        }
                    });
        });
    }
}