package com.example.rentini.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rentini.ChatActivity;
import com.example.rentini.R;
import com.example.rentini.models.Property;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

public class PropertyDetailActivity extends AppCompatActivity {

    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView roomsTextView;
    private TextView surfaceTextView;
    private TextView priceTextView;
    private TextView typeTextView;
    private TextView ownerNameTextView;
    private TextView wifi;  // Changed from wifiIcon
    private TextView parking;  // Changed from parkingIcon
    private TextView kitchen;  // Changed from kitchenIcon
    private TextView ac;  // Changed from airConditionerIcon
    private TextView furnished;
    private ImageView backButton;  // Changed from ImageButton to ImageView
    private MaterialButton contact0, contact1, contact2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_detail);

        initializeViews();
        loadPropertyDetails();
        setupClickListeners();
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
        contact0 = findViewById(R.id.contact0);
        contact1 = findViewById(R.id.contact1);
        contact2 = findViewById(R.id.contact2);
    }

    private void loadPropertyDetails() {
        // Get property details from intent
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        int rooms = getIntent().getIntExtra("rooms", 0);
        double surface = getIntent().getDoubleExtra("surface", 0.0);
        double price = getIntent().getDoubleExtra("price", 0.0);
        String type = getIntent().getStringExtra("type");
        String ownerId = getIntent().getStringExtra("ownerId");

        // Get facilities
        boolean hasWifi = getIntent().getBooleanExtra("hasWifi", false);
        boolean hasParking = getIntent().getBooleanExtra("hasParking", false);
        boolean hasKitchen = getIntent().getBooleanExtra("hasKitchen", false);
        boolean hasAirConditioner = getIntent().getBooleanExtra("hasAirConditioner", false);
        boolean isFurnished = getIntent().getBooleanExtra("isFurnished", false);

        // Set values to views
        titleTextView.setText(title);
        descriptionTextView.setText(description);
        roomsTextView.setText(String.format("%d Rooms", rooms));
        surfaceTextView.setText(String.format("%.1f m²", surface));
        priceTextView.setText(String.format("$%.2f", price));
        typeTextView.setText("/" + type);

        // Set facility icons visibility
        wifi.setVisibility(hasWifi ? View.VISIBLE : View.GONE);
        parking.setVisibility(hasParking ? View.VISIBLE : View.GONE);
        kitchen.setVisibility(hasKitchen ? View.VISIBLE : View.GONE);
        ac.setVisibility(hasAirConditioner ? View.VISIBLE : View.GONE);
        furnished.setVisibility(isFurnished ? View.VISIBLE : View.GONE);

        // Load owner name from Firestore
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
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> finish());
        
        contact0.setOnClickListener(v -> {
            String ownerId = getIntent().getStringExtra("ownerId");
            Intent chatIntent = new Intent(this, ChatActivity.class);
            chatIntent.putExtra("otherUserId", ownerId);
            startActivity(chatIntent);
        });
    }
}
