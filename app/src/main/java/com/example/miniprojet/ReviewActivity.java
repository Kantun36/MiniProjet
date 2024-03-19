package com.example.miniprojet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniprojet.model.Restaurant;
import com.example.miniprojet.model.Review;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {

    private EditText reviewComment;
    private RatingBar reviewRating;
    private ImageView reviewPhoto;
    private Button takePhotoButton;
    private Button submitReviewButton;

    private Uri photoUri;

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

        reviewComment = findViewById(R.id.review_text);
        reviewRating = findViewById(R.id.review_rating);
        reviewPhoto = findViewById(R.id.review_image);
        takePhotoButton = findViewById(R.id.take_photo_button);
        submitReviewButton = findViewById(R.id.submit_review_button);

        Button retourButton = findViewById(R.id.exitButton);
        retourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    if (photoFile != null) {
                        photoUri = FileProvider.getUriForFile(ReviewActivity.this,
                                "com.example.miniprojet.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(takePictureIntent, 1);
                    }
                }
            }
        });

        submitReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = reviewComment.getText().toString();
                float rating = reviewRating.getRating();

                // Upload review data to Firebase
                uploadReviewData(comment, rating);
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

    private void uploadReviewData(String comment, float rating) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        String restaurantId = intent.getStringExtra("restaurantId");

        Review review = new Review(comment, rating);

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference photoRef = storageRef.child("restaurants/" + restaurantId + "/reviews/" + review.getId() + ".jpg");

        UploadTask uploadTask = photoRef.putFile(photoUri);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return photoRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    review.setPhotoUrl(downloadUri.toString());

                    DocumentReference restaurantRef = db.collection("restaurant").document(restaurantId);

                    review.setTimestamp(new Timestamp(new Date()));

                    restaurantRef.collection("avis").document(review.getId()).set(review)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ReviewActivity.this, "Avis soumis avec succès !", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ReviewActivity.this, "Échec de l'envoi de l'avis", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(ReviewActivity.this, "Échec du téléchargement de l'image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Glide.with(this).load(photoUri).into(reviewPhoto);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        String mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}