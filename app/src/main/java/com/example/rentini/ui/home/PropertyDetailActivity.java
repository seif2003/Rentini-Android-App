package com.example.rentini.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


import com.example.rentini.R;
import com.example.rentini.adapters.ImageGalleryAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.api.IMapController;

public class PropertyDetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView roomsTextView;
    private TextView surfaceTextView;
    private TextView priceTextView;
    private TextView typeTextView;
    private TextView ownerNameTextView;
    private TextView wifi;
    private TextView parking;
    private TextView kitchen;
    private TextView ac;
    private TextView furnished;
    private ImageView backButton;
    private MaterialButton contact2;
    private RecyclerView imageGalleryRecyclerView;
    private ImageView fullSizeImageView;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_detail);

        initializeViews();
        loadPropertyDetails();
        setupClickListeners();
        setupImageGallery();
    }

    private void initializeViews() {
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        roomsTextView = findViewById(R.id.roomsTextView);
        surfaceTextView = findViewById(R.id.surfaceTextView);
        priceTextView = findViewById(R.id.priceTextView);
        typeTextView = findViewById(R.id.typeTextView);
        ownerNameTextView = findViewById(R.id.ownerNameTextView);
        wifi = findViewById(R.id.wifi);
        parking = findViewById(R.id.parking);
        kitchen = findViewById(R.id.kitchen);
        ac = findViewById(R.id.ac);
        furnished= findViewById(R.id.furnished);
        backButton = findViewById(R.id.backButton);
        contact2 = findViewById(R.id.contact2);
        mapView = findViewById(R.id.mapView);
    }

    private void setupImageGallery() {
        imageGalleryRecyclerView = findViewById(R.id.imageGalleryRecyclerView);
        //fullSizeImageView = findViewById(R.id.fullSizeImageView);

        List<String> images = getIntent().getStringArrayListExtra("images");
        
        if (images != null && !images.isEmpty()) {
            ImageGalleryAdapter adapter = new ImageGalleryAdapter(
                this, 
                images, 
                bitmap -> {
                    // Optional: Show full-size image when gallery image is clicked
                    // fullSizeImageView.setImageBitmap(bitmap);
                }
            );

            LinearLayoutManager layoutManager = new LinearLayoutManager(
                this, 
                LinearLayoutManager.HORIZONTAL, 
                false
            );
            
            imageGalleryRecyclerView.setLayoutManager(layoutManager);
            imageGalleryRecyclerView.setAdapter(adapter);
        }
    }

    private void loadPropertyDetails() {
        String propertyId = getIntent().getStringExtra("propertyId");
        
        if (propertyId == null) {
            finish();
            return;
        }

        FirebaseFirestore.getInstance()
            .collection("properties")
            .document(propertyId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (!documentSnapshot.exists()) {
                    finish();
                    return;
                }

                // Get property owner's ID
                String ownerId = documentSnapshot.getString("userId");
                
                // Get other property details as before
                String title = documentSnapshot.getString("title");
                String description = documentSnapshot.getString("description");
                long rooms = documentSnapshot.getLong("rooms");
                double surface = documentSnapshot.getDouble("surface");
                double price = documentSnapshot.getDouble("price");
                String type = documentSnapshot.getString("type");
                boolean hasWifi = documentSnapshot.getBoolean("hasWifi");
                boolean hasParking = documentSnapshot.getBoolean("hasParking");
                boolean hasKitchen = documentSnapshot.getBoolean("hasKitchen");
                boolean hasAirConditioner = documentSnapshot.getBoolean("hasAirConditioning");
                boolean isFurnished = documentSnapshot.getBoolean("hasFurnished");
                double latitude = documentSnapshot.getDouble("latitude");
                double longitude = documentSnapshot.getDouble("longitude");
                List<String> images = (List<String>) documentSnapshot.get("images");

                // Update UI with property details
                titleTextView.setText(title);
                descriptionTextView.setText(description);
                roomsTextView.setText(String.format("%d Rooms", rooms));
                surfaceTextView.setText(String.format("%.1f m²", surface));
                priceTextView.setText(String.format("$%.2f", price));
                typeTextView.setText("/" + type);

                wifi.setVisibility(hasWifi ? View.VISIBLE : View.GONE);
                parking.setVisibility(hasParking ? View.VISIBLE : View.GONE);
                kitchen.setVisibility(hasKitchen ? View.VISIBLE : View.GONE);
                ac.setVisibility(hasAirConditioner ? View.VISIBLE : View.GONE);
                furnished.setVisibility(isFurnished ? View.VISIBLE : View.GONE);

                // Load owner details
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(ownerId)
                    .get()
                    .addOnSuccessListener(userSnapshot -> {
                        if (userSnapshot.exists()) {
                            String ownerName = userSnapshot.getString("firstName") + " " + 
                                             userSnapshot.getString("lastName");
                            ownerNameTextView.setText(ownerName);
                        }
                    });

                // Setup map
                Configuration.getInstance().setUserAgentValue(getPackageName());
                mapView.setTileSource(TileSourceFactory.MAPNIK);
                mapView.setMultiTouchControls(true);

                // Create marker at property location 
                GeoPoint propertyLocation = new GeoPoint(latitude, longitude);
                Marker marker = new Marker(mapView);
                marker.setPosition(propertyLocation);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                mapView.getOverlays().add(marker);

                // Center map on property location
                IMapController mapController = mapView.getController();
                mapController.setZoom(15.0);
                mapController.setCenter(propertyLocation);

                // Refresh map
                mapView.invalidate();

                // Setup image gallery if images exist
                if (images != null && !images.isEmpty()) {
                    setupImageGallery(images);
                }

                // Now fetch the owner's phone number from users collection
                if (ownerId != null) {
                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(ownerId)
                        .get()
                        .addOnSuccessListener(userDoc -> {
                            if (userDoc.exists()) {
                                String ownerPhone = userDoc.getString("phone");
                                // Update the UI with the phone number
                                contact2.setOnClickListener(v -> {
                                    // Create intent to dial the phone number
                                    Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                                    dialIntent.setData(Uri.parse("tel:" + ownerPhone));
                                    startActivity(dialIntent);
                                });
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error fetching owner details", Toast.LENGTH_SHORT).show();
                        });
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Error loading property details", Toast.LENGTH_SHORT).show();
                finish();
            });
    }

    // Modified setupImageGallery to accept images parameter
    private void setupImageGallery(List<String> images) {
        imageGalleryRecyclerView = findViewById(R.id.imageGalleryRecyclerView);
        
        ImageGalleryAdapter adapter = new ImageGalleryAdapter(
            this, 
            images, 
            bitmap -> {
                // Optional: Show full-size image when gallery image is clicked
                // fullSizeImageView.setImageBitmap(bitmap);
            }
        );

        LinearLayoutManager layoutManager = new LinearLayoutManager(
            this, 
            LinearLayoutManager.HORIZONTAL, 
            false
        );
        
        imageGalleryRecyclerView.setLayoutManager(layoutManager);
        imageGalleryRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());


    }
}
