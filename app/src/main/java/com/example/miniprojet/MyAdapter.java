package com.example.miniprojet;
import com.bumptech.glide.Glide;
import com.example.miniprojet.model.Restaurant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<Restaurant> itemList;
    private Context context;

    public MyAdapter(Context context, List<Restaurant> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.resto_card_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant item = itemList.get(position);
        holder.textViewTitle.setText(item.getTitle());
        holder.cuisineTypeTextView.setText(item.getType());
        holder.addressTextView.setText(item.getAddress());

        item.getImg().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .into(holder.restaurantImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.w("MainActivity", "Erreur lors de la récupération des données", exception);
            }
        });

        holder.ratingTextView.setText(item.getRating().toString());
        holder.onlineReservationTextView.setText(item.getReservation().toString());
        holder.openingTimeTextView.setText(item.getOpening().toString());

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        ImageView restaurantImageView;
        TextView addressTextView;
        TextView cuisineTypeTextView;
        TextView ratingTextView;
        TextView onlineReservationTextView;
        TextView openingTimeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.titleCard);
            cuisineTypeTextView = itemView.findViewById(R.id.type);
            restaurantImageView = itemView.findViewById(R.id.img);
            addressTextView = itemView.findViewById(R.id.address);
            openingTimeTextView = itemView.findViewById(R.id.ouverture);
            onlineReservationTextView = itemView.findViewById(R.id.reservable);
            ratingTextView = itemView.findViewById(R.id.rating);

        }
    }
}
