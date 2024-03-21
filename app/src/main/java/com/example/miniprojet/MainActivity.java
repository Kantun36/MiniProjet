package com.example.miniprojet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;

import com.example.miniprojet.adapter.RestaurantAdapter;
import com.example.miniprojet.adapter.RestaurantCallback;
import com.example.miniprojet.model.Restaurant;
import com.example.miniprojet.model.Review;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.recycleReview);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        restaurantData(new RestaurantCallback() {
            @Override
            public void onCallback(List<Restaurant> restaurants) {
                RestaurantAdapter adapter = new RestaurantAdapter(MainActivity.this, restaurants);
                recyclerView.setAdapter(adapter);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();

                if (itemId == R.id.boutonHome) {
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    return true;
                } else if (itemId == R.id.boutonMap) {
                    startActivity(new Intent(MainActivity.this, MapActivity.class));
                    return true;
                }

                return false;
            }
        });

    }
    public void restaurantData(RestaurantCallback callback) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        CollectionReference restaurantCollection = db.collection("restaurant");
        List<Restaurant> restaurants = new ArrayList<>();

        restaurantCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String restaurantId = document.getId();
                        String restaurantName = document.getString("nom");
                        String restaurantType = document.getString("type_cuisine");
                        String restaurantAddress = document.getString("adresse");
                        Boolean restaurantReservation = document.getBoolean("reservation_en_ligne");
                        Date restaurantOpening = document.getDate("horaire_ouverture");
                        GeoPoint location = document.getGeoPoint("coordonnées");
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                        } else {
                        } String restaurantImg = document.getString("image");

                        String restaurantCapacity = (document.getString("capacite"));
                        String restaurantDescription = document.getString("description");
                        String restaurantPrixMoy = (document.getString("prix_moyen"));
                        String restaurantTel = (document.getString("tel"));


                        // Référence à l'image dans Firebase Storage
                        StorageReference imageRef = storageRef.child(restaurantImg);

                        // Télécharger l'image en tant que fichier temporaire
                        final File localFile;
                        try {
                            localFile = File.createTempFile("tempImage", "jpg");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        imageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                // Convertir le fichier temporaire en chaîne de caractères Base64
                                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                                byte[] byteArray = byteArrayOutputStream.toByteArray();
                                String imageString = Base64.encodeToString(byteArray, Base64.DEFAULT);

                                // Calculer la moyenne des ratings pour ce restaurant
                                calculateAverageRatingForRestaurant(restaurantId, new OnAverageRatingCalculatedListener() {
                                    @Override
                                    public void onAverageRatingCalculated(double averageRating) {
                                        // Ajouter le restaurant avec la moyenne des ratings à la liste
                                        restaurants.add(new Restaurant(restaurantId, restaurantName, restaurantType, restaurantAddress, restaurantDescription, restaurantPrixMoy, restaurantTel, restaurantCapacity, imageString, averageRating, restaurantReservation, restaurantOpening, location));

                                        // Vérifier si tous les restaurants ont été ajoutés à la liste
                                        if (restaurants.size() == queryDocumentSnapshots.size()) {
                                            // Appeler le callback avec la liste remplie
                                            callback.onCallback(restaurants);
                                        }
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("MainActivity", "Erreur lors de la récupération des données", e);
                });
    }

    public void calculateAverageRatingForRestaurant(String restaurantId, final OnAverageRatingCalculatedListener listener) {
        DocumentReference restaurantRef = db.collection("restaurant").document(restaurantId);
        restaurantRef.collection("avis").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    double totalRating = 0;
                    int numberOfReviews = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            double rating = document.getDouble("rating");
                            totalRating += rating;
                            numberOfReviews++;
                        }
                    }
                    double averageRating = (numberOfReviews > 0) ? totalRating / numberOfReviews : 0;
                    // Retourne la moyenne via le listener
                    listener.onAverageRatingCalculated(averageRating);
                } else {
                    // En cas d'erreur, retourne une moyenne de 0
                    listener.onAverageRatingCalculated(0);
                }
            }
        });
    }

    // Interface pour écouter le calcul de la moyenne du rating
    public interface OnAverageRatingCalculatedListener {
        void onAverageRatingCalculated(double averageRating);
    }


}