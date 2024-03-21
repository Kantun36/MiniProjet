package com.example.miniprojet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapActivity extends AppCompatActivity {

    private MapView mapView;
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

        Marker restaurantMarker = new Marker(mapView);
        restaurantMarker.setPosition(new GeoPoint(0, 0));
        restaurantMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        restaurantMarker.setTitle("Restaurant");
        mapView.getOverlays().add(restaurantMarker);
        mapView.getController().setZoom(4);
    }
}

