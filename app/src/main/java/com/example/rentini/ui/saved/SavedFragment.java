package com.example.rentini.ui.saved;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentini.R;
import com.example.rentini.adapters.PropertyAdapter;
import com.example.rentini.databinding.FragmentSavedBinding;
import com.example.rentini.models.Property;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SavedFragment extends Fragment {
    private RecyclerView recyclerView;
    private PropertyAdapter propertyAdapter;
    private List<Property> savedProperties;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved, container, false);

        recyclerView = view.findViewById(R.id.recycler_saved);
        savedProperties = new ArrayList<>();

        // Initialiser Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Configurer le RecyclerView
        propertyAdapter = new PropertyAdapter(getContext(), savedProperties);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(propertyAdapter);

        // Charger les propriétés sauvegardées
        loadSavedProperties();

        // Mettre à jour la vue avec les nouvelles données
        propertyAdapter.notifyDataSetChanged();
        return view;
    }

    private void loadSavedProperties() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Log.e("SavedFragment", "User not authenticated.");
            return;
        }

        db.collection("users")
                .document(currentUser.getUid())
                .collection("savedProperties")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    savedProperties.clear();

                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d("SavedFragment", "No saved properties found.");
                        propertyAdapter.notifyDataSetChanged();
                        return;
                    }

                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Property property = document.toObject(Property.class);
                        if (property != null) {
                            property.setSaved(true);
                            savedProperties.add(property);
                        }
                    }

                    propertyAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("SavedFragment", "Error loading saved properties", e));
    }

    public void removeFromSaved(Property property) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) return;

        db.collection("users")
                .document(currentUser.getUid())
                .collection("savedProperties")
                .document(property.getId()) // Utiliser l'ID de la propriété
                .delete()
                .addOnSuccessListener(aVoid -> {
                    savedProperties.remove(property); // Supprimer localement
                    propertyAdapter.notifyDataSetChanged(); // Notifier l'adaptateur pour rafraîchir la vue
                    Log.d("SavedFragment", "Propriété supprimée des favoris.");
                })
                .addOnFailureListener(e -> Log.e("SavedFragment", "Erreur lors de la suppression de la propriété.", e));
    }


}