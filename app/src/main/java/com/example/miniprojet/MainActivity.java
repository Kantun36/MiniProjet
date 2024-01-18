package com.example.miniprojet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MainActivity extends AppCompatActivity {
    private final FirebaseFirestore db;

    MainActivity(FirebaseFirestore db) {
        this.db = db;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Récupérer la référence de la collection "restaurant" dans Firestore
        CollectionReference restaurantCollection = db.collection("restaurant");

        // Effectuer une requête pour récupérer tous les documents de la collection
        restaurantCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // La requête a réussi, vous pouvez traiter les résultats ici
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        // Accédez aux données de chaque document
                        String restaurantName = document.getString("nom"); // Remplacez "nom" par le champ réel que vous souhaitez afficher
                        Log.d("MainActivity", "Nom du restaurant : " + restaurantName);
                    }
                })
                .addOnFailureListener(e -> {
                    // La requête a échoué, gérez l'erreur ici
                    Log.w("MainActivity", "Erreur lors de la récupération des données", e);
                });
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}