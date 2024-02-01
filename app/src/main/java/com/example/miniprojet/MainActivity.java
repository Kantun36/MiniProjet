package com.example.miniprojet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.animation.BounceInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.type.DateTime;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.recycle);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        restaurantData(new RestaurantCallback() {
            @Override
            public void onCallback(List<Restaurant> restaurants) {
                Log.d("MainActivity", "MDR : " + restaurants);
            }
        });

        Log.d("MainActivity", "MDR : ");
        MyAdapter adapter = new MyAdapter(this, restaurantData());
        recyclerView.setAdapter(adapter);

    }
    public void restaurantData(RestaurantCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference restaurantCollection = db.collection("restaurant");
        List<Restaurant> restaurants = new ArrayList<>();

        restaurantCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String restaurantName = document.getString("nom");
                        String restaurantType = document.getString("type_cuisine");
                        String restaurantAddress = document.getString("adresse");
                        Double restaurantRating = document.getDouble("eval_moyenne");
                        Boolean restaurantReservation = document.getBoolean("reservation_en_ligne");
                        Timestamp restaurantOpening = document.getTimestamp("horaire_ouverture");
                        restaurants.add(new Restaurant(restaurantName, restaurantType, restaurantAddress, 0, restaurantRating, restaurantReservation, restaurantOpening));
                    }

                    // Call the callback with the filled list
                    callback.onCallback(restaurants);
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.w("MainActivity", "Erreur lors de la récupération des données", e);
                });
    }

}