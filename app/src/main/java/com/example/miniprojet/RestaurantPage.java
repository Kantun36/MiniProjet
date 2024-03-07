package com.example.miniprojet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.w3c.dom.Text;

public class RestaurantPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference restaurantCollection = db.collection("restaurant");
        restaurantCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String restaurantName = document.getString("nom");
                        Log.d("RestaurantPage", "Nom du restaurant : " + restaurantName);
                        String restaurantType = document.getString("type_cuisine");
                        String restaurantAddress = document.getString("adresse");
                        Double restaurantRating = document.getDouble("eval_moyenne");
                        Boolean restaurantReservation = document.getBoolean("reservation_en_ligne");
                        Timestamp restaurantOpening = document.getTimestamp("horaire_ouverture");
                        String restaurantDescription = document.getString("description");
                        String restaurantPhone = document.getString("telephone");
                        Double restaurantPrice = document.getDouble("prix_moyen");
                        Double restaurantCapacity = document.getDouble("capacite");

                        TextView title = findViewById(R.id.title);
                        TextView description = findViewById(R.id.description);
                        TextView adresse = findViewById(R.id.adresse);
                        TextView telephone = findViewById(R.id.telephone);
                        TextView horaire = findViewById(R.id.horaire);
                        TextView prix = findViewById(R.id.prix);
                        TextView type = findViewById(R.id.type);
                        TextView capacite = findViewById(R.id.capacite);

                        title.setText(restaurantName);
                        description.setText(restaurantDescription);
                        adresse.setText(restaurantAddress);
                        telephone.setText(restaurantPhone);
                        horaire.setText(restaurantOpening.toDate().toString());
                        type.setText(restaurantType);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("RestaurantPage", "Erreur lors de la récupération des données", e);
                });
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resto_page);
    }

}
