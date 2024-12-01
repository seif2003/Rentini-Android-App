package com.example.rentini.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import com.example.rentini.Filtre;
import com.example.rentini.R;
import com.example.rentini.adapters.PropertyAdapter;
import com.example.rentini.models.Property;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeFragment extends Fragment {
    private ImageButton filtreButton;
    private RecyclerView recyclerView;
    private PropertyAdapter adapter;
    private List<Property> propertyList = new ArrayList<>();
    private List<Property> originalPropertyList = new ArrayList<>();
    private FirebaseFirestore db;
    private ActivityResultLauncher<Intent> filterActivityResultLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initializeViews(view);
        setupFilterLauncher();
        fetchPropertiesFromFirestore();

        return view;
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        filtreButton = view.findViewById(R.id.filter);

        filtreButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Filtre.class);
            filterActivityResultLauncher.launch(intent);
        });
    }

    private void setupFilterLauncher() {
        filterActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String selectedPeriod = data.getStringExtra("selectedPeriod");
                            double minPrice = data.getDoubleExtra("minPrice", 0.0);
                            double maxPrice = data.getDoubleExtra("maxPrice", Double.MAX_VALUE);
                            String[] selectedFacilities = data.getStringArrayExtra("selectedFacilities");

                            applyFilters(selectedPeriod, minPrice, maxPrice, selectedFacilities);
                        }
                    }
                });
    }

    private void fetchPropertiesFromFirestore() {
        db = FirebaseFirestore.getInstance();
        db.collection("properties")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    propertyList.clear();
                    originalPropertyList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Property property = createPropertyFromDocument(document);
                        propertyList.add(property);
                        originalPropertyList.add(property);
                    }

                    fetchSavedPropertiesForUser();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to fetch properties: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private Property createPropertyFromDocument(QueryDocumentSnapshot document) {
        return new Property(
                document.getId(),
                document.getString("title"),
                document.getString("description"),
                document.getString("type"),
                document.getDouble("price"),
                document.getLong("rooms").intValue(),
                document.getDouble("surface"),
                document.getDouble("latitude"),
                document.getDouble("longitude"),
                document.getString("userId"),
                Boolean.TRUE.equals(document.getBoolean("hasWifi")),
                Boolean.TRUE.equals(document.getBoolean("hasParking")),
                Boolean.TRUE.equals(document.getBoolean("hasKitchen")),
                Boolean.TRUE.equals(document.getBoolean("hasAirConditioning")),
                Boolean.TRUE.equals(document.getBoolean("hasFurnished")),
                (List<String>) document.get("images")
        );
    }

    private void fetchSavedPropertiesForUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        db.collection("users")
                .document(currentUser.getUid())
                .collection("savedProperties")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot savedDoc : queryDocumentSnapshots) {
                        String savedPropertyId = savedDoc.getId();
                        propertyList.stream()
                                .filter(p -> p.getId().equals(savedPropertyId))
                                .findFirst()
                                .ifPresent(p -> p.setSaved(true));
                    }

                    setupAdapter();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to fetch saved properties: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void setupAdapter() {
        adapter = new PropertyAdapter(getContext(), propertyList);
        recyclerView.setAdapter(adapter);
    }

    private void applyFilters(String selectedPeriod, double minPrice, double maxPrice, String[] selectedFacilities) {
        Log.d("FilterDebug", "Selected Period: " + selectedPeriod);
        Log.d("FilterDebug", "Total Original Properties: " + originalPropertyList.size());

        List<Property> filteredList = originalPropertyList.stream()
                .filter(property -> {
                    boolean periodMatch = selectedPeriod == null ||
                            selectedPeriod.isEmpty() ||
                            property.getType().equalsIgnoreCase(selectedPeriod);

                    Log.d("FilterDebug", "Property: " + property.getTitle() +
                            ", Type: " + property.getType() +
                            ", Period Match: " + periodMatch);

                    return periodMatch &&
                            (property.getPrice() >= minPrice && property.getPrice() <= maxPrice) &&
                            matchesFacilities(property, selectedFacilities);
                })
                .collect(Collectors.toList());

        Log.d("FilterDebug", "Filtered Properties Count: " + filteredList.size());

        adapter = new PropertyAdapter(getContext(), filteredList);
        recyclerView.setAdapter(adapter);

        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "No properties match the selected filters", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean matchesFacilities(Property property, String[] selectedFacilities) {
        if (selectedFacilities == null || selectedFacilities.length == 0) {
            return true;
        }

        for (String facility : selectedFacilities) {
            boolean facilitiesMatch = false;

            switch (facility) {
                case "WiFi":
                    facilitiesMatch = property.isHasWifi();
                    break;
                case "Parking":
                    facilitiesMatch = property.isHasParking();
                    break;
                case "Kitchen":
                    facilitiesMatch = property.isHasKitchen();
                    break;
                case "Air Conditioner":
                    facilitiesMatch = property.isHasAirConditioning();
                    break;
                case "Furnished":
                    facilitiesMatch = property.isHasFurnished();
                    break;
                default:
                    facilitiesMatch = false;
            }

            if (!facilitiesMatch) {
                return false;
            }
        }
        return true;
    }


    private boolean matchesPeriod(Property property, String selectedPeriod) {
        if (selectedPeriod == null || selectedPeriod.isEmpty()) {
            return true;
        }

        // Trim and convert to uppercase to ensure exact matching
        String propertyType = property.getType().trim().toUpperCase();
        String selectedType = selectedPeriod.trim().toUpperCase();

        Log.d("PeriodDebug", "Property Type: " + propertyType +
                ", Selected Type: " + selectedType +
                ", Matches: " + propertyType.equals(selectedType));

        return propertyType.equals(selectedType);
    }
}