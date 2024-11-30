package com.example.rentini;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class Filtre extends AppCompatActivity {

    // UI Elements
    private ChipGroup periodChipGroup, facilitiesChipGroup;
    private Button resetButton, showResultsButton;
    private ImageButton backButton;
    private List<String> selectedFacilities = new ArrayList<>();
    private String selectedPeriod = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter);

        // UI Initialization
        periodChipGroup = findViewById(R.id.periodChipGroup);
        facilitiesChipGroup = findViewById(R.id.facilitiesChipGroup);
        resetButton = findViewById(R.id.resetButton);
        showResultsButton = findViewById(R.id.showResultsButton);
        backButton = findViewById(R.id.backButton);

        // Back Button Functionality
        backButton.setOnClickListener(v -> {
            finish();  // Go back to the previous activity
        });

        // Reset All Button Functionality
        resetButton.setOnClickListener(v -> resetFilters());

        // Show Results Button Functionality
        showResultsButton.setOnClickListener(v -> showResults());

        // Handle Facilities Chip Clicks
        for (int i = 0; i < facilitiesChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) facilitiesChipGroup.getChildAt(i);
            chip.setOnClickListener(v -> toggleFacilitySelection(chip));
        }

        // Handle Period Chip Clicks (Single Selection)
        periodChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = findViewById(checkedId);
            if (chip != null) {
                selectedPeriod = chip.getText().toString();
                Toast.makeText(Filtre.this, "Selected Period: " + selectedPeriod, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Toggle Facility Selection
    private void toggleFacilitySelection(Chip chip) {
        String facility = chip.getText().toString();
        if (selectedFacilities.contains(facility)) {
            selectedFacilities.remove(facility);
            chip.setChipBackgroundColorResource(android.R.color.darker_gray);
            chip.setTextColor(getResources().getColor(android.R.color.black));
        } else {
            selectedFacilities.add(facility);
            chip.setChipBackgroundColorResource(R.color.purple_500);
            chip.setTextColor(getResources().getColor(android.R.color.white));
        }
        Toast.makeText(Filtre.this, "Selected Facilities: " + selectedFacilities.toString(), Toast.LENGTH_SHORT).show();
    }

    // Reset Filters
    private void resetFilters() {
        // Reset Period Selection
        periodChipGroup.clearCheck();
        selectedPeriod = "";

        // Reset Facilities Chips
        for (int i = 0; i < facilitiesChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) facilitiesChipGroup.getChildAt(i);
            chip.setChipBackgroundColorResource(android.R.color.darker_gray);
            chip.setTextColor(getResources().getColor(android.R.color.black));
        }
        selectedFacilities.clear();

        // Reset the Price Range Slider (Optional)
        // priceRangeSlider.setValues(1200f, 3000f);

        // Notify user that the filters are reset
        Toast.makeText(this, "Filters reset to initial state", Toast.LENGTH_SHORT).show();
    }

    // Show Results (For demo, you can add logic to display results)
    private void showResults() {
        String selectedFacilitiesText = selectedFacilities.toString();
        String message = "Period: " + selectedPeriod + "\nFacilities: " + selectedFacilitiesText;
        Toast.makeText(Filtre.this, message, Toast.LENGTH_LONG).show();
    }
}
