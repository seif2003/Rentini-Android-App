package com.example.rentini.ui.saved;

import com.example.rentini.models.Property;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyRepository {
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public void saveProperty(Property property) {
        String userId = auth.getCurrentUser().getUid();

        Map<String, Object> savedProperty = new HashMap<>();
        savedProperty.put("propertyId", property.getId());
        savedProperty.put("savedAt", FieldValue.serverTimestamp());

        db.collection("users")
                .document(userId)
                .collection("savedProperties")
                .add(savedProperty)
                .addOnSuccessListener(documentReference -> {
                    // Property saved successfully
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

    public void fetchSavedProperties(OnSavedPropertiesLoadedListener listener) {
        String userId = auth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("savedProperties")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> savedPropertyIds = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        savedPropertyIds.add(doc.getString("propertyId"));
                    }

                    // Fetch full property details using these IDs
                    fetchPropertiesDetails(savedPropertyIds, listener);
                });
    }

    private void fetchPropertiesDetails(List<String> propertyIds, OnSavedPropertiesLoadedListener listener) {
        // Fetch full property details from main properties collection
        // This is a simplified example
        List<Property> savedProperties = new ArrayList<>();
        for (String id : propertyIds) {
            db.collection("properties")
                    .document(id)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Property property = documentSnapshot.toObject(Property.class);
                        savedProperties.add(property);

                        if (savedProperties.size() == propertyIds.size()) {
                            listener.onSavedPropertiesLoaded(savedProperties);
                        }
                    });
        }
    }

    // Listener interface
    public interface OnSavedPropertiesLoadedListener {
        void onSavedPropertiesLoaded(List<Property> savedProperties);
    }
}