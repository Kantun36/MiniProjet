package com.example.miniprojet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniprojet.model.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private List<Review> itemList;
    private Context context;

    public ReviewAdapter(Context context, List<Review> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_card_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review item = itemList.get(position);
        holder.reviewTexte.setText(item.getComment());
        holder.ratingBarTexte.setRating(item.getRating());

    }
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView reviewTexte;
        RatingBar ratingBarTexte;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            reviewTexte = itemView.findViewById(R.id.reviewTexte);
            ratingBarTexte = itemView.findViewById(R.id.ratingBar);

        }
    }
}

