package com.example.rentini.ui.properties;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.rentini.R;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditPropertyActivity extends AppCompatActivity {

    private EditText editTitle, editDescription, editRooms, editSurface, editPrice;
    private Button btnSave;
    private String propertyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_property);

        // Initialisation des vues
        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        editRooms = findViewById(R.id.editRooms);
        editSurface = findViewById(R.id.editSurface);
        editPrice = findViewById(R.id.editPrice);
        btnSave = findViewById(R.id.btnSave);

        // Récupérer les données envoyées depuis l'activité précédente
        Intent intent = getIntent();
        propertyId = intent.getStringExtra("propertyId");
        String title = intent.getStringExtra("propertyTitle");
        String description = intent.getStringExtra("propertyDescription");
        int rooms = intent.getIntExtra("propertyRooms", 0);
        int surface = intent.getIntExtra("propertySurface", 0);
        double price = intent.getDoubleExtra("propertyPrice", 0);

        // Afficher les données dans les champs de modification
        editTitle.setText(title);
        editDescription.setText(description);
        editRooms.setText(String.valueOf(rooms));
        editSurface.setText(String.valueOf(surface));
        editPrice.setText(String.valueOf(price));

        // Gestion du bouton "Enregistrer"
        btnSave.setOnClickListener(v -> saveProperty());
    }

    // Fonction pour enregistrer les modifications de la propriété
    private void saveProperty() {
        String updatedTitle = editTitle.getText().toString();
        String updatedDescription = editDescription.getText().toString();
        int updatedRooms = Integer.parseInt(editRooms.getText().toString());
        double updatedSurface = Double.parseDouble(editSurface.getText().toString());
        double updatedPrice = Double.parseDouble(editPrice.getText().toString());

        // Vérification des champs
        if (updatedTitle.isEmpty() || updatedDescription.isEmpty() || updatedRooms <= 0 || updatedSurface <= 0 || updatedPrice <= 0) {
            Toast.makeText(this, "Tous les champs doivent être remplis correctement", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mise à jour dans la base de données (Firestore par exemple)
        updatePropertyInFirestore(updatedTitle, updatedDescription, updatedRooms, updatedSurface, updatedPrice);
    }

    // Fonction pour mettre à jour la propriété dans Firestore
    private void updatePropertyInFirestore(String updatedTitle, String updatedDescription, int updatedRooms, double updatedSurface, double updatedPrice) {
        // Vous pouvez utiliser Firestore pour mettre à jour la propriété dans la base de données
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Mettre à jour la propriété dans Firestore
        db.collection("properties").document(propertyId)
                .update("title", updatedTitle,
                        "description", updatedDescription,
                        "rooms", updatedRooms,
                        "surface", updatedSurface,
                        "price", updatedPrice)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditPropertyActivity.this, "Propriété mise à jour avec succès", Toast.LENGTH_SHORT).show();
                    finish(); // Retour à l'écran précédent après la mise à jour
                })
                .addOnFailureListener(e -> Toast.makeText(EditPropertyActivity.this, "Erreur de mise à jour : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}