package com.example.rentini;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rentini.databinding.ActivityAddPropertyBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

import com.example.rentini.models.Property;

import java.util.HashMap;
import java.util.Map;

public class AddPropertyActivity extends AppCompatActivity {
    private ActivityAddPropertyBinding binding;
    private MapView map;
    private GeoPoint selectedLocation;
    private Marker currentMarker;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static final int PERMISSION_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure OSMDroid
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        binding = ActivityAddPropertyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        checkPermissions();
        initializeMap();
        setupPropertyTypeSpinner();
        setupSubmitButton();

        ImageButton ib = findViewById(R.id.close);

        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);*/
                finish();
            }
        });
    }

    private void setupPropertyTypeSpinner() {
        String[] types = new String[]{"Day", "Week", "Month"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this, 
            android.R.layout.simple_dropdown_item_1line, 
            types
        );
        binding.propertyTypeDropdown.setAdapter(adapter);
    }

    private void setupSubmitButton() {
        binding.submit.setOnClickListener(v -> submitProperty());
    }

    private void submitProperty() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please sign in to add a property", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a Map for Firestore
        Map<String, Object> propertyMap = new HashMap<>();
        try {
            propertyMap.put("title", binding.propertyTitleInput.getText().toString());
            propertyMap.put("description", binding.descriptionInput.getText().toString());
            propertyMap.put("type", binding.propertyTypeDropdown.getText().toString());
            propertyMap.put("price", Double.parseDouble(binding.priceInput.getText().toString()));
            propertyMap.put("rooms", Integer.parseInt(binding.roomsInput.getText().toString()));
            propertyMap.put("surface", Double.parseDouble(binding.surfaceInput.getText().toString()));
            propertyMap.put("latitude", selectedLocation.getLatitude());
            propertyMap.put("longitude", selectedLocation.getLongitude());
            propertyMap.put("userId", currentUser.getUid());
            propertyMap.put("timestamp", FieldValue.serverTimestamp());

            // Facilities
            propertyMap.put("hasWifi", binding.wifiCheckbox.isChecked());
            propertyMap.put("hasParking", binding.parkingCheckbox.isChecked());
            propertyMap.put("hasKitchen", binding.kitchenCheckbox.isChecked());
            propertyMap.put("hasAirConditioning", binding.airConditionerCheckbox.isChecked());
            propertyMap.put("hasFurnished", binding.furnishedCheckbox.isChecked());

            // Show loading state
            binding.submit.setEnabled(false);

            // Upload to Firestore
            db.collection("properties")
                .add(propertyMap)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Property added successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    binding.submit.setEnabled(true);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeMap() {
        // Set user agent to prevent tile loading issues
        Configuration.getInstance().setUserAgentValue(getPackageName());

        map = binding.mapView;
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.setBuiltInZoomControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(15.0);

        // Set default location (e.g., Paris)
        GeoPoint startPoint = new GeoPoint(48.8583, 2.2944);
        mapController.setCenter(startPoint);

        // Add click listener for marker placement
        map.getOverlays().add(new Overlay() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
                Projection proj = mapView.getProjection();
                GeoPoint tappedPoint = (GeoPoint) proj.fromPixels((int) e.getX(), (int) e.getY());
                
                updateMarkerPosition(tappedPoint);
                return true;
            }
        });
    }

    private void updateMarkerPosition(GeoPoint point) {
        selectedLocation = point;

        if (currentMarker == null) {
            currentMarker = new Marker(map);
            map.getOverlays().add(currentMarker);
        }

        currentMarker.setPosition(point);
        currentMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.invalidate();
    }

    private void checkPermissions() {
        String[] permissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }

    private boolean hasPermissions(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }
}