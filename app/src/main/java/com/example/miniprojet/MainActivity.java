package com.example.miniprojet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.example.miniprojet.model.Restaurant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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

    }
    public void restaurantData(RestaurantCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                        Double restaurantRating = document.getDouble("eval_moyenne");
                        Boolean restaurantReservation = document.getBoolean("reservation_en_ligne");
                        Date restaurantOpening = document.getDate("horaire_ouverture");
                        String restaurantImg = document.getString("image");

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

                                // Ajouter le restaurant avec la chaîne de caractères de l'image à la liste
                                restaurants.add(new Restaurant(restaurantId,restaurantName, restaurantType, restaurantAddress,restaurantDescription,restaurantPrixMoy,restaurantTel,restaurantCapacity, imageString, restaurantRating, restaurantReservation, restaurantOpening));

                                // Vérifier si tous les restaurants ont été ajoutés à la liste
                                if (restaurants.size() == queryDocumentSnapshots.size()) {
                                    // Appeler le callback avec la liste remplie
                                    callback.onCallback(restaurants);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Gérer l'erreur de téléchargement ici
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    // Gérer l'erreur de récupération des données ici
                    Log.w("MainActivity", "Erreur lors de la récupération des données", e);
                });
    }

}