package com.example.miniprojet;

import android.content.Intent;


import android.os.Bundle;


import android.util.Log;

import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.miniprojet.adapter.ReviewAdapter;
import com.example.miniprojet.adapter.ReviewCallback;
import com.example.miniprojet.model.Review;


import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import java.util.List;

public class ReviewActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        RecyclerView recyclerView = findViewById(R.id.recycleReview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewData(new ReviewCallback() {
            @Override
            public void onCallback(List<Review> reviews) {
                ReviewAdapter adapter = new ReviewAdapter(ReviewActivity.this, reviews);
                recyclerView.setAdapter(adapter);
            }
        });


        Button retourButton = findViewById(R.id.exitButton);
        retourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        String restaurantId = intent.getStringExtra("restaurantId");
        Button createReviewButton = findViewById(R.id.createReviewButton);
        createReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReviewActivity.this, ReviewCreateActivity.class);
                intent.putExtra("restaurantId", restaurantId);
                startActivity(intent);
            }
        });



    }
    public void reviewData(ReviewCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        String restaurantId = intent.getStringExtra("restaurantId");
        DocumentReference restaurant = db.collection("restaurant").document(restaurantId);
        List<Review> reviews = new ArrayList<>();

        restaurant.collection("avis").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String comment = document.getString("comment");
                        Float rating = document.getDouble("rating").floatValue();

                                // Ajouter le restaurant avec la chaîne de caractères de l'image à la liste
                        reviews.add(new Review(comment,rating));

                                // Vérifier si tous les restaurants ont été ajoutés à la liste
                                if (reviews.size() == queryDocumentSnapshots.size()) {
                                    // Appeler le callback avec la liste remplie
                                    callback.onCallback(reviews);
                                }

                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("MainActivity", "Erreur lors de la récupération des données", e);
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Rafraîchir les données
        refreshData();
    }

    private void refreshData() {
        RecyclerView recyclerView = findViewById(R.id.recycleReview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewData(new ReviewCallback() {
            @Override
            public void onCallback(List<Review> reviews) {
                ReviewAdapter adapter = new ReviewAdapter(ReviewActivity.this, reviews);
                recyclerView.setAdapter(adapter);
            }
        });
    }

}