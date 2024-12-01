package com.example.rentini.ui.profile;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.rentini.PersonalDetails;
import com.example.rentini.R;
import com.example.rentini.SignIn;
import com.example.rentini.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {
    private ProfileViewModel mViewModel;
    private FirebaseAuth mAuth;
    private FragmentProfileBinding binding;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        mAuth = FirebaseAuth.getInstance();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Get TextViews
        TextView profileName = view.findViewById(R.id.profile_name);
        TextView profileEmail = view.findViewById(R.id.profile_email);

        // Get current user from Firebase
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Get email from Firebase Auth
            profileEmail.setText(currentUser.getEmail());

            // Get name from Firestore
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String firstName = documentSnapshot.getString("firstName");
                        String lastName = documentSnapshot.getString("lastName");
                        String fullName = firstName + " " + lastName;
                        profileName.setText(fullName);
                    }
                });
        }

        // Your existing code
        Button logoutButton = view.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(getActivity(), SignIn.class));
            getActivity().finish();
        });

        binding.personalDetailsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PersonalDetails.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}