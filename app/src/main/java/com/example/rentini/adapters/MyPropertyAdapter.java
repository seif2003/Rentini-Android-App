package com.example.rentini.adapters;

import static java.lang.String.valueOf;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentini.AddPropertyActivity;
import com.example.rentini.R;
import com.example.rentini.models.Property;
import com.example.rentini.ui.properties.EditPropertyActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MyPropertyAdapter extends RecyclerView.Adapter<MyPropertyAdapter.PropertyViewHolder> {
    private final FirebaseFirestore db;
    private List<Property> propertyList;
    private Context context;

    public MyPropertyAdapter(List<Property> propertyList, Context context) {
        this.propertyList = propertyList;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_property_item, parent, false);
        return new PropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        Property property = propertyList.get(position);

        holder.title.setText(property.getTitle());
        holder.description.setText(property.getDescription());
        holder.rooms.setText(property.getRooms() + " pièces");
        holder.surface.setText(property.getSurface() + " m²");
        holder.price.setText(String.format("%.2f TND", property.getPrice()));
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

        // Bouton Modifier
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditPropertyActivity.class);
            intent.putExtra("propertyId", property.getId());
            intent.putExtra("propertyTitle", property.getTitle());
            intent.putExtra("propertyDescription", property.getDescription());
            intent.putExtra("propertyRooms", property.getRooms());
            intent.putExtra("propertySurface", property.getSurface());
            intent.putExtra("propertyPrice", property.getPrice());
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirmation")
                    .setMessage("Voulez-vous vraiment supprimer cette propriété ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        // Montrer un indicateur de chargement si nécessaire
                        showLoading();

                        db.collection("properties").document(property.getId())
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    // Obtenir la position actuelle
                                    int currentPosition = holder.getAdapterPosition();
                                    if (currentPosition != RecyclerView.NO_POSITION) {
                                        // Supprimer l'élément de la liste locale
                                        propertyList.remove(currentPosition);

                                        // Mettre à jour uniquement l'élément supprimé et les suivants
                                        notifyItemRemoved(currentPosition);
                                        notifyItemRangeChanged(currentPosition, propertyList.size());

                                        Toast.makeText(context, "Propriété supprimée avec succès", Toast.LENGTH_SHORT).show();
                                    }
                                    hideLoading();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Erreur lors de la suppression : " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                    hideLoading();
                                });
                    })
                    .setNegativeButton("Non", null)
                    .show();
        });

    }
    // Méthode pour mettre à jour la liste complète si nécessaire
    public void updateList(List<Property> newList) {
        this.propertyList.clear();
        this.propertyList.addAll(newList);
        notifyDataSetChanged();
    }

    // Méthode optionnelle pour montrer un indicateur de chargement
    private void showLoading() {
        // Implémenter si nécessaire
    }

    // Méthode optionnelle pour cacher l'indicateur de chargement
    private void hideLoading() {
        // Implémenter si nécessaire
    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }

    static class PropertyViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, location, rooms, surface, price;
        ImageView propertyImageView;
        ImageButton btnEdit, btnDelete;

        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.property_title);
            description = itemView.findViewById(R.id.property_description);
            location = itemView.findViewById(R.id.localisation);
            rooms = itemView.findViewById(R.id.property_rooms);
            surface = itemView.findViewById(R.id.property_surface);
            price = itemView.findViewById(R.id.property_price);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            propertyImageView = itemView.findViewById(R.id.property_image);
        }
    }
}
