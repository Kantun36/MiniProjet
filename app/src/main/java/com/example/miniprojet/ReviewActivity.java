package com.example.miniprojet;

import android.Manifest;

import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;


import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;


import android.view.Surface;
import android.view.TextureView;

import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;



import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.miniprojet.model.Review;


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Arrays;

import java.util.ArrayList;

import java.util.Date;
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
                    // Gérer l'erreur de récupération des données ici
                    Log.w("MainActivity", "Erreur lors de la récupération des données", e);
                });
    }








}