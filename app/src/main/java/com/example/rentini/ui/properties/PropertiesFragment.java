package com.example.rentini.ui.properties;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.example.rentini.AddPropertyActivity;
import com.example.rentini.R;
import com.example.rentini.adapters.MyPropertyAdapter;
import com.example.rentini.models.Property;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
public class PropertiesFragment extends Fragment {
    private RecyclerView recyclerView;
    private MyPropertyAdapter adapter;
    private List<Property> propertyList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db; // Changement ici

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_properties, container, false);

        Button addPropertyButton = view.findViewById(R.id.add_property);
        addPropertyButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddPropertyActivity.class);
            startActivity(intent);
        });

        // Initialiser Firebase Auth et Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Changement ici

        // Configurer RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        propertyList = new ArrayList<>();
        adapter = new MyPropertyAdapter(propertyList, getContext());
        recyclerView.setAdapter(adapter);

        // Récupérer les propriétés
        loadProperties();

        return view;
    }

    private void loadProperties() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = currentUser.getUid();
        // Utilisation de Firestore à la place de Realtime Database
        db.collection("properties")
                .whereEqualTo("userId", userId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Erreur : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (value != null) {
                        propertyList.clear();
                        for (QueryDocumentSnapshot document : value) {
                            // Convertir le document en objet Property
                            Property property = document.toObject(Property.class);
                            property.setId(document.getId()); // Sauvegarder l'ID du document
                            propertyList.add(property);
                        }
                        adapter.notifyDataSetChanged();
                        if (propertyList.isEmpty()) {
                            Toast.makeText(getContext(), "Aucune propriété trouvée.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}


