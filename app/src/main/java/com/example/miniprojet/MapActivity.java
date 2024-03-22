package com.example.miniprojet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.miniprojet.model.Restaurant;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Définir un écouteur pour les éléments du menu de navigation inférieure
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();

                if (itemId == R.id.boutonHome) {
                    startActivity(new Intent(MapActivity.this, MainActivity.class));
                    return true;
                } else if (itemId == R.id.boutonMap) {
                    startActivity(new Intent(MapActivity.this, MapActivity.class));
                    return true;
                }

                return false;
            }
        });


        mapView = findViewById(R.id.mapView);

        mapView.setTileSource(TileSourceFactory.MAPNIK); // Utilisation de la source de tuiles MAPNIK (OpenStreetMap)

        mapView.setBuiltInZoomControls(true);



        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        CollectionReference restaurantCollection = db.collection("restaurant");
        List<Restaurant> restaurants = new ArrayList<>();

        restaurantCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String restaurantId = document.getId();
                        Log.w("MapActivity", "Restaurant " + restaurantId);
                        String restaurantName = document.getString("nom");
                        com.google.firebase.firestore.GeoPoint location = document.getGeoPoint("coordonnées");
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            Log.w("MapActivity", "Restaurant " + restaurantName + " at " + latitude + ", " + longitude);

                            Marker restaurantMarker = new Marker(mapView);
                            restaurantMarker.setPosition(new GeoPoint(latitude, longitude));
                            restaurantMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                            restaurantMarker.setTitle(restaurantName);

                            mapView.getOverlays().add(restaurantMarker);
                            mapView.getController().setZoom(4);
                        }
                    }
                });

    }   }

