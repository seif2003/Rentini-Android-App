package com.example.rentini;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button b = findViewById(R.id.button2);
        Intent i = new Intent(this, SignUp.class);
        b.setOnClickListener(view -> startActivity(i));

        TextInputEditText email = findViewById(R.id.textInputEditText);
        TextInputEditText password = findViewById(R.id.passwordField);
        Button login = findViewById(R.id.logout_button);

        login.setOnClickListener(view -> {
            String emailText = email.getText().toString().trim();
            String passwordText = password.getText().toString().trim();

            // Input validation
            if (emailText.isEmpty()) {
                email.setError("Email is required");
                email.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                email.setError("Please enter a valid email");
                email.requestFocus();
                return;
            }

            if (passwordText.isEmpty()) {
                password.setError("Password is required");
                password.requestFocus();
                return;
            }

            // Disable login button to prevent multiple clicks
            login.setEnabled(false);

            // Sign in with Firebase
            mAuth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                if (user.isEmailVerified()) {
                                    // Navigate to main activity
                                    Intent mainIntent = new Intent(SignIn.this, MainActivity.class);
                                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                    finish();
                                } else {
                                    user.sendEmailVerification();
                                    Toast.makeText(SignIn.this,
                                            "Please verify your email first. Verification email sent.",
                                            Toast.LENGTH_LONG).show();
                                    login.setEnabled(true);
                                }
                            }
                        } else {
                            // If sign in fails, display a message to the user
                            String errorMessage = task.getException() != null ?
                                    task.getException().getMessage() :
                                    "Authentication failed";
                            Toast.makeText(SignIn.this,
                                    "Login failed: " + errorMessage,
                                    Toast.LENGTH_LONG).show();
                            login.setEnabled(true);
                        }
                    });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            // Navigate to main activity if user is already signed in
            startActivity(new Intent(SignIn.this, MainActivity.class));
            finish();
        }
    }
}