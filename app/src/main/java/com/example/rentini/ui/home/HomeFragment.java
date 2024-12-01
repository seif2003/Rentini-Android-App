package com.example.rentini.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rentini.Filtre;
import com.example.rentini.R;
import com.example.rentini.adapters.PropertyAdapter;
import com.example.rentini.models.Property;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    ImageButton filtreButton;
    private RecyclerView recyclerView;
    private PropertyAdapter adapter;
    private List<Property> propertyList;
    private FirebaseFirestore db;
    private ActivityResultLauncher<Intent> filterActivityResultLauncher;
    private TextView myLocation;
    private LocationManager locationManager;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize views
        myLocation = view.findViewById(R.id.myLocation);

        // Setup location services
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        // Check and request permissions
        if (checkLocationPermission()) {
            getCurrentLocation();
        }

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize the property list
        propertyList = new ArrayList<>();

        // Fetch properties from Firestore
        fetchPropertiesFromFirestore();

        return view;
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    private void getCurrentLocation() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    5000, 5, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            updateLocationText(location);
                        }

                        @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
                        @Override public void onProviderEnabled(String provider) {}
                        @Override public void onProviderDisabled(String provider) {}
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void updateLocationText(Location location) {
        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1
            );
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                String locationText = address.getLocality() + ", " +
                        address.getAdminArea();
                myLocation.setText(locationText);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
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
