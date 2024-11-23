package com.example.rentini.ui.home;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rentini.R;
import com.example.rentini.models.Property;

public class PropertyDetailActivity extends AppCompatActivity {

    private TextView titleTextView, descriptionTextView, priceTextView, roomsTextView, surfaceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_detail);

        // Initialiser les vues
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        priceTextView = findViewById(R.id.priceTextView);
        roomsTextView = findViewById(R.id.roomsTextView);
        surfaceTextView = findViewById(R.id.surfaceTextView);

        // Obtenez les données passées à partir de l'intent
        Property property = (Property) getIntent().getSerializableExtra("property");

        // Affichez les détails de la propriété
        if (property != null) {
            titleTextView.setText(property.getTitle());
            descriptionTextView.setText(property.getDescription());
            priceTextView.setText(String.format("TND %.2f", property.getPrice()));
            roomsTextView.setText(String.format("Rooms: %d", property.getRooms()));
            surfaceTextView.setText(String.format("Surface: %.2f sqm", property.getSurface()));
        }
    }
}
