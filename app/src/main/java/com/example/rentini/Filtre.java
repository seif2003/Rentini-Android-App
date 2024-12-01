package com.example.rentini;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;

public class Filtre extends AppCompatActivity {

    // UI Elements
    private Chip chipDaily, chipMonthly, chipWeekly;
    private EditText minPriceEditText, maxPriceEditText;
    private Button resetButton, showResultsButton;
    private ImageButton backButton;
    private ChipGroup facilitiesChipGroup;

    // Data holders
    private List<String> selectedFacilities = new ArrayList<>();
    private String selectedPeriod = "";
    private double minPrice = 0.0;
    private double maxPrice = Double.MAX_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter);

        // Initialize UI Components
        initializeViews();
        setupListeners();
    }

    // Initialize View References
    private void initializeViews() {
        chipDaily = findViewById(R.id.chipDaily);
        facilitiesChipGroup = findViewById(R.id.facilitiesChipGroup);
        chipMonthly = findViewById(R.id.chipMonthly);
        chipWeekly = findViewById(R.id.chipWeekly);

        minPriceEditText = findViewById(R.id.minPriceEditText);
        maxPriceEditText = findViewById(R.id.maxPriceEditText);

        resetButton = findViewById(R.id.resetButton);
        showResultsButton = findViewById(R.id.showResultsButton);
        backButton = findViewById(R.id.backButton);
    }

    // Setup Click Listeners
    private void setupListeners() {
        // Back Button Functionality
        backButton.setOnClickListener(v -> finish());

        // Reset All Button Functionality
        resetButton.setOnClickListener(v -> resetFilters());

        // Show Results Button Functionality
        showResultsButton.setOnClickListener(v -> showResults());

        // Chip Selection Listeners
        chipDaily.setOnClickListener(v -> handlePeriodSelection(chipDaily));
        chipMonthly.setOnClickListener(v -> handlePeriodSelection(chipMonthly));
        chipWeekly.setOnClickListener(v -> handlePeriodSelection(chipWeekly));

        // Facilities Chip Clicks
        setupFacilitiesChips();
    }

    // Setup Facilities Chip Interactions
    private void setupFacilitiesChips() {
        for (int i = 0; i < facilitiesChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) facilitiesChipGroup.getChildAt(i);
            chip.setOnClickListener(v -> toggleFacilitySelection(chip));
        }
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
    }

    private void handlePeriodSelection(Chip selectedChip) {
        // Reset all chips first
        Chip[] allChips = {chipDaily, chipMonthly, chipWeekly};
        for (Chip chip : allChips) {
            chip.setChipBackgroundColorResource(android.R.color.darker_gray);
            chip.setTextColor(getResources().getColor(android.R.color.black));
        }

        // Apply selected chip styling
        selectedChip.setChipBackgroundColorResource(R.color.purple_500);
        selectedChip.setTextColor(getResources().getColor(android.R.color.white));

        // Explicitly set the selectedPeriod to match Firebase data
        if (selectedChip == chipDaily) {
            selectedPeriod = "Day";  // Ensure it's exactly "Day"
        } else if (selectedChip == chipMonthly) {
            selectedPeriod = "Month";
        } else if (selectedChip == chipWeekly) {
            selectedPeriod = "Week";
        }

        Log.d("PeriodSelection", "Selected Period: " + selectedPeriod);
    }

    // Reset Filters
    private void resetFilters() {
        // Reset Period Selection
        Chip[] allChips = {chipDaily, chipMonthly, chipWeekly};
        for (Chip chip : allChips) {
            chip.setChipBackgroundColorResource(android.R.color.darker_gray);
            chip.setTextColor(getResources().getColor(android.R.color.black));
        }
        selectedPeriod = "";

        // Reset Price Fields
        minPriceEditText.setText("");
        maxPriceEditText.setText("");
        minPrice = 0.0;
        maxPrice = Double.MAX_VALUE;

        // Reset Facilities
        selectedFacilities.clear();
        for (int i = 0; i < facilitiesChipGroup.getChildCount(); i++) {
            Chip chip = (Chip) facilitiesChipGroup.getChildAt(i);
            chip.setChipBackgroundColorResource(android.R.color.darker_gray);
            chip.setTextColor(getResources().getColor(android.R.color.black));
        }

        Toast.makeText(this, "Filters reset to initial state", Toast.LENGTH_SHORT).show();
    }

    private void showResults() {
        // Validate and retrieve price inputs
        String minPriceStr = minPriceEditText.getText().toString();
        String maxPriceStr = maxPriceEditText.getText().toString();

        try {
            minPrice = minPriceStr.isEmpty() ? 0.0 : Double.parseDouble(minPriceStr);
            maxPrice = maxPriceStr.isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxPriceStr);

            if (minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
                Toast.makeText(this, "Invalid price range. Please correct it.", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price input. Please enter valid numbers.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create an Intent to return filter data
        Intent resultIntent = new Intent();
        resultIntent.putExtra("selectedPeriod", selectedPeriod);
        resultIntent.putExtra("minPrice", minPrice);
        resultIntent.putExtra("maxPrice", maxPrice);
        resultIntent.putExtra("selectedFacilities", selectedFacilities.toArray(new String[0]));

        // Set the result and finish the activity
        setResult(RESULT_OK, resultIntent);
        finish();
    }


}