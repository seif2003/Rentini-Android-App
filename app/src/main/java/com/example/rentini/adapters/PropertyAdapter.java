package com.example.rentini.adapters;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentini.R;
import com.example.rentini.models.Property;
import com.example.rentini.ui.home.PropertyDetailActivity;
import com.example.rentini.Filtre;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder> {

    private Context context;
    private List<Property> propertyList;

    public PropertyAdapter(Context context, List<Property> propertyList) {
        this.context = context;
        this.propertyList = propertyList;
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.property_row, parent, false);
        return new PropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        Property property = propertyList.get(position);

        holder.titleTextView.setText(property.getTitle());
        holder.descriptionTextView.setText(property.getDescription());
        holder.priceTextView.setText(String.format("%,.2f TND / %s", property.getPrice(), property.getType()));
        holder.roomsTextView.setText("Rooms: " + property.getRooms());
        holder.surfaceTextView.setText("Surface: " + property.getSurface() + " m²");
        // Convertir la première image en bitmap
        if (property.getImages() != null && !property.getImages().isEmpty()) {
            try {
                String image = property.getImages().get(0);
                Log.d("image",image);
                byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                if (decodedByte != null) {
                    holder.propertyImageView.setImageBitmap(decodedByte);
                } else {
                    Log.e("PropertyAdapter", "Failed to decode image bitmap");
                    // Set a placeholder image
                    holder.propertyImageView.setImageResource(R.drawable.detailsmaision);
                }
            } catch (IllegalArgumentException e) {
                Log.e("PropertyAdapter", "Error decoding base64: " + e.getMessage());
                // Set a placeholder image
                holder.propertyImageView.setImageResource(R.drawable.detailsmaision);
            }
        }

        // Construire une chaîne pour les équipements
        StringBuilder features = new StringBuilder();
        if (property.isHasWifi()) features.append("WiFi, ");
        if (property.isHasParking()) features.append("Parking, ");
        if (property.isHasKitchen()) features.append("Kitchen, ");
        if (property.isHasAirConditioning()) features.append("Air Conditioning, ");
        if (property.isHasFurnished()) features.append("Furnished");

        // Supprimer la dernière virgule
        String featuresText = features.toString().trim();
        if (featuresText.endsWith(",")) {
            featuresText = featuresText.substring(0, featuresText.length() - 1);
        }

       // holder.featuresTextView.setText(featuresText);


        // Gérer le clic sur l'élément
        String finalFeaturesText = featuresText;
        holder.itemView.setOnClickListener(v -> {
            Log.d("PropertyAdapter", "Clicked on property: " + property.getTitle());
            Intent intent = new Intent(context, PropertyDetailActivity.class);
            intent.putExtra("rooms", property.getRooms());
            intent.putExtra("surface", property.getSurface());
            intent.putExtra("price", property.getPrice());
            intent.putExtra("title", property.getTitle());
            intent.putExtra("description", property.getDescription());
            intent.putExtra("type", property.getType());
            intent.putExtra("ownerId", property.getUserId());
            intent.putExtra("hasWifi", property.isHasWifi());
            intent.putExtra("hasParking", property.isHasParking());
            intent.putExtra("hasKitchen", property.isHasKitchen());
            intent.putExtra("hasAirConditioner", property.isHasAirConditioning());
            intent.putExtra("isFurnished", property.isHasFurnished());
            context.startActivity(intent);
        });

        // Set initial save button state
        updateSaveButtonState(holder.saveButton, property);

        holder.saveButton.setOnClickListener(v -> {
            // Si la propriété est sauvegardée, la supprimer, sinon l'ajouter
            if (property.isSaved()) {
                // Supprimer de la liste des favoris
                updateSaveButtonState(holder.saveButton, property);
                removeFromSaved(property);
            } else {
                // Ajouter à la liste des favoris
                updateSaveButtonState(holder.saveButton, property);
                savePropertyToUserFavorites(property);
            }

            // Basculer l'état de la propriété
            property.setSaved(!property.isSaved());
            updateSaveButtonState(holder.saveButton, property);
        });


    }
    public void removeFromSaved(Property property) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(currentUser.getUid())
                .collection("savedProperties")
                .document(property.getId()) // Utiliser l'ID de la propriété
                .delete()
                .addOnSuccessListener(aVoid -> {
                    notifyDataSetChanged(); // Rafraîchir l'adaptateur
                    Log.d("PropertyAdapter", "Propriété supprimée des favoris.");
                })
                .addOnFailureListener(e -> Log.e("PropertyAdapter", "Erreur lors de la suppression de la propriété.", e));
    }

    private void updateSaveButtonState(ImageButton saveButton, Property property) {
        if (property.isSaved()) {
            saveButton.setImageResource(R.drawable.redheart); // Red filled heart
        } else {
            saveButton.setImageResource(R.drawable.heart); // Outline heart
        }
    }

    private void savePropertyToUserFavorites(Property property) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.e("PropertyAdapter", "User not authenticated.");
            return;
        }

        String propertyId = property.getId();
        if (propertyId == null || propertyId.isEmpty()) {
            Log.e("PropertyAdapter", "Property ID is null or empty.");
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(currentUser.getUid())
                .collection("savedProperties")
                .document(propertyId)
                .set(property)
                .addOnSuccessListener(aVoid -> Log.d("PropertyAdapter", "Property saved successfully."))
                .addOnFailureListener(e -> Log.e("PropertyAdapter", "Error saving property: ", e));
    }


    @Override
    public int getItemCount() {
        return propertyList.size();
    }

    static class PropertyViewHolder extends RecyclerView.ViewHolder {
        ImageView propertyImageView;
        TextView titleTextView;
        TextView descriptionTextView;
        TextView priceTextView;
        TextView roomsTextView;
        TextView surfaceTextView;
        TextView featuresTextView;
        ImageButton saveButton, filtreButton;

        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            propertyImageView = itemView.findViewById(R.id.property_image);
            titleTextView = itemView.findViewById(R.id.property_title);
            descriptionTextView = itemView.findViewById(R.id.property_description);
            priceTextView = itemView.findViewById(R.id.property_price);
            roomsTextView = itemView.findViewById(R.id.property_rooms);
            surfaceTextView = itemView.findViewById(R.id.property_surface);
            //featuresTextView = itemView.findViewById(R.id.property_features);
            saveButton = itemView.findViewById(R.id.save_property);
            filtreButton= itemView.findViewById(R.id.filter);
        }
    }

}
