package com.example.rentini.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rentini.R;
import java.util.ArrayList;
import java.util.List;

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.ImageViewHolder> {
    private Context context;
    private List<String> base64Images;
    private OnImageClickListener imageClickListener;

    public interface OnImageClickListener {
        void onImageClick(Bitmap bitmap);
    }

    public ImageGalleryAdapter(Context context, List<String> base64Images, OnImageClickListener listener) {
        this.context = context;
        this.base64Images = base64Images;
        this.imageClickListener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_gallery_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String base64Image = base64Images.get(position);
        
        try {
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            if (decodedByte != null) {
                holder.imageView.setImageBitmap(decodedByte);
                
                holder.imageView.setOnClickListener(v -> {
                    if (imageClickListener != null) {
                        imageClickListener.onImageClick(decodedByte);
                    }
                });
            } else {
                holder.imageView.setImageResource(R.drawable.detailsmaision);
            }
        } catch (Exception e) {
            holder.imageView.setImageResource(R.drawable.detailsmaision);
        }
    }

    @Override
    public int getItemCount() {
        return base64Images.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.gallery_image_view);
        }
    }
}