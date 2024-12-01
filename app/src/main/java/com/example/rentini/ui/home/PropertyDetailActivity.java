package com.example.rentini.ui.home;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        int rooms = getIntent().getIntExtra("rooms", 0);
        double surface = getIntent().getDoubleExtra("surface", 0.0);
        double price = getIntent().getDoubleExtra("price", 0.0);
        String type = getIntent().getStringExtra("type");
        String ownerId = getIntent().getStringExtra("ownerId");

        boolean hasWifi = getIntent().getBooleanExtra("hasWifi", false);
        boolean hasParking = getIntent().getBooleanExtra("hasParking", false);
        boolean hasKitchen = getIntent().getBooleanExtra("hasKitchen", false);
        boolean hasAirConditioner = getIntent().getBooleanExtra("hasAirConditioner", false);
        boolean isFurnished = getIntent().getBooleanExtra("isFurnished", false);

        List<String> images = getIntent().getStringArrayListExtra("images");

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

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(ownerId)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String ownerName = documentSnapshot.getString("firstName") + " " + 
                                     documentSnapshot.getString("lastName");
                    ownerNameTextView.setText(ownerName);
                }
            });

        // Initialize map
        Configuration.getInstance().setUserAgentValue(getPackageName());
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        
        // Get coordinates from intent
        double latitude = getIntent().getDoubleExtra("latitude", 0);
        double longitude = getIntent().getDoubleExtra("longitude", 0);

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
