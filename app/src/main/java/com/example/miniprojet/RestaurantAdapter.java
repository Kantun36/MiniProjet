package com.example.miniprojet;
import com.bumptech.glide.Glide;
import com.example.miniprojet.model.Restaurant;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private List<Restaurant> itemList;
    private Context context;

    public RestaurantAdapter(Context context, List<Restaurant> itemList) {
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

        // Récupérer la chaîne de caractères Base64 de l'image à partir de votre objet
        String imageString = item.getImg();

        // Convertir la chaîne de caractères Base64 en tableau de bytes
        byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);

        // Charger l'image à partir du tableau de bytes à l'aide de Glide
        Glide.with(context)
                .load(imageBytes)
                .into(holder.restaurantImageView);

        holder.ratingTextView.setText(item.getRating().toString());
        holder.onlineReservationTextView.setText(item.getReservation().toString());
        holder.openingTimeTextView.setText(item.getOpening().toString());

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        ImageView restaurantImageView;
        TextView addressTextView;
        TextView cuisineTypeTextView;
        TextView ratingTextView;
        TextView onlineReservationTextView;
        TextView openingTimeTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.reviewTexte);
            cuisineTypeTextView = itemView.findViewById(R.id.type);
            restaurantImageView = itemView.findViewById(R.id.img);
            addressTextView = itemView.findViewById(R.id.address);
            openingTimeTextView = itemView.findViewById(R.id.ouverture);
            onlineReservationTextView = itemView.findViewById(R.id.reservable);
            ratingTextView = itemView.findViewById(R.id.rating);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Get the selected restaurant
                        Restaurant selectedRestaurant = itemList.get(position);

                        Intent intent = new Intent(context, RestaurantPage.class);
                        intent.putExtra("restaurantInfo", selectedRestaurant);
                        context.startActivity(intent);
                    }
                }
            });

        }
    }
}
