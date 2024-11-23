package com.example.rentini.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentini.R;
import com.example.rentini.models.Property;
import com.example.rentini.ui.home.PropertyDetailActivity;

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

        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            propertyImageView = itemView.findViewById(R.id.property_image);
            titleTextView = itemView.findViewById(R.id.property_title);
            descriptionTextView = itemView.findViewById(R.id.property_description);
            priceTextView = itemView.findViewById(R.id.property_price);
            roomsTextView = itemView.findViewById(R.id.property_rooms);
            surfaceTextView = itemView.findViewById(R.id.property_surface);
            //featuresTextView = itemView.findViewById(R.id.property_features);
        }
    }
}
