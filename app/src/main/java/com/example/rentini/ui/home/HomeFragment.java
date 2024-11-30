package com.example.rentini.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.rentini.R;
import com.example.rentini.adapters.PropertyAdapter;
import com.example.rentini.models.Property;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PropertyAdapter adapter;
    private List<Property> propertyList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize the property list
        propertyList = new ArrayList<>();

        // Fetch properties from Firestore
        fetchPropertiesFromFirestore();

        return view;
    }

    private void fetchPropertiesFromFirestore() {
        CollectionReference propertiesRef = db.collection("properties");

        propertiesRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot document : documents) {
                            // Get the property data from Firestore document
                            String title = document.getString("title");
                            String description = document.getString("description");
                            String type = document.getString("type");
                            double price = document.getDouble("price");
                            int rooms = document.getLong("rooms").intValue();
                            double surface = document.getDouble("surface");
                            double latitude = document.getDouble("latitude");
                            double longitude = document.getDouble("longitude");
                            String userId = document.getString("userId");
                            boolean hasWifi = document.getBoolean("hasWifi");
                            boolean hasParking = document.getBoolean("hasParking");
                            boolean hasKitchen = document.getBoolean("hasKitchen");
                            boolean hasAirConditioning = document.getBoolean("hasAirConditioning");
                            boolean hasFurnished = document.getBoolean("hasFurnished");
                            String documentId = document.getId();
                            List<String> images = (List<String>) document.get("images");


                            // Create a Property object
                            Property property = new Property(
                                    documentId,
                                    title,
                                    description,
                                    type,
                                    price,
                                    rooms,
                                    surface,
                                    latitude,
                                    longitude,
                                    userId,
                                    hasWifi,
                                    hasParking,
                                    hasKitchen,
                                    hasAirConditioning,
                                    hasFurnished,
                                    images
                            );

                            // Add the property to the list
                            propertyList.add(property);
                        }

                        // Now, fetch the saved properties for the current user
                        fetchSavedPropertiesForUser();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch properties: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchSavedPropertiesForUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(currentUser.getUid())
                .collection("savedProperties")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots != null) {
                        List<DocumentSnapshot> savedDocuments = queryDocumentSnapshots.getDocuments();

                        // Go through the saved properties and mark them as saved in the propertyList
                        for (DocumentSnapshot savedDoc : savedDocuments) {
                            String savedPropertyId = savedDoc.getId();

                            for (Property property : propertyList) {
                                if (property.getId().equals(savedPropertyId)) {
                                    property.setSaved(true); // Mark property as saved
                                }
                            }
                        }

                        // After updating the saved state, set the adapter to the RecyclerView
                        adapter = new PropertyAdapter(getContext(), propertyList);
                        recyclerView.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch saved properties: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
