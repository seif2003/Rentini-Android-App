package com.example.rentini.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rentini.R;
import com.example.rentini.models.Property;
import com.google.android.material.button.MaterialButton;

public class PropertyDetailActivity extends AppCompatActivity {

    private ImageView backButton;
    private MaterialButton contact0, contact1, contact2;
    private TextView roomsTextView ,surfaceTextView,titleTextView, locationTextView, priceTextView, descriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_detail);

        // Initializing views
        backButton = findViewById(R.id.backButton);
        contact0 = findViewById(R.id.contact0);
        contact1 = findViewById(R.id.contact1);
        contact2 = findViewById(R.id.contact2);
        titleTextView = findViewById(R.id.titleTextView);
        locationTextView = findViewById(R.id.locationTextView);
        priceTextView = findViewById(R.id.priceTextView);
        ImageView propertyImage = findViewById(R.id.propertyImage);
        ImageView ownerImage = findViewById(R.id.ownerImage);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        roomsTextView = findViewById(R.id.roomsTextView);
        surfaceTextView = findViewById(R.id.surfaceTextView);

        String rooms = getIntent().getStringExtra("rooms");
        String surface = getIntent().getStringExtra("surface");
        String description = getIntent().getStringExtra("description");
        Log.d("PropertyDetail", "rooms: " + rooms + ", surface: " + surface + ", description: " + description);

        if (description != null) {
        roomsTextView.setText(rooms);
        surfaceTextView.setText(surface);
        descriptionTextView.setText(description);



           /* titleTextView.setText(property.getTitle());
            priceTextView.setText(String.format("%,.2f TND", property.getPrice()));
            descriptionTextView.setText(property.getDescription());
            surfaceTextView.setText(String.format("%,.2f m²", property.getSurface()));
            roomsTextView.setText(String.format("%d rooms", property.getRooms()));*/





        }

        // Back Button logic (navigate back to the previous activity)
        backButton.setOnClickListener(v -> {
            finish();
        });

        // Contact Button 0 (Message Intent - Explicit Intent)
        contact0.setOnClickListener(v -> {

        });

        // Contact Button 1 (Call Intent - Implicit Intent)
        contact1.setOnClickListener(v -> {
            // Assuming the phone number is already defined
            String phoneNumber = "+1234567890"; // Replace with actual phone number
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        });

        // Contact Button 2 (Call Intent - Implicit Intent with different styling)
        contact2.setOnClickListener(v -> {
            // Assuming the phone number is already defined
            String phoneNumber = "+1234567890"; // Replace with actual phone number
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        });
    }
}
