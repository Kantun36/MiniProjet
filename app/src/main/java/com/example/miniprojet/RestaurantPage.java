package com.example.miniprojet;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.miniprojet.model.Restaurant;


import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;

import org.osmdroid.views.overlay.Marker;
import org.osmdroid.util.GeoPoint;


public class RestaurantPage extends AppCompatActivity{
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resto_page);

        // Initialisation de la configuration osmdroid (nécessaire avant l'initialisation de MapView)
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        // Initialisation du MapView
        mapView = findViewById(R.id.mapView);

        // Configuration de la source de tuiles
        mapView.setTileSource(TileSourceFactory.MAPNIK); // Utilisation de la source de tuiles MAPNIK (OpenStreetMap)

        // Activation des contrôles de zoom
        mapView.setBuiltInZoomControls(true);



        // Récupérer l'objet Restaurant sélectionné à partir de l'intent
        Restaurant selectedRestaurant = getIntent().getParcelableExtra("restaurantInfo");

        // Récupérer les références aux TextViews
        TextView title = findViewById(R.id.title);
        TextView description = findViewById(R.id.description);
        TextView adresse = findViewById(R.id.adresse);
        TextView telephone = findViewById(R.id.telephone);
        TextView horaire = findViewById(R.id.horaire);
        TextView prix = findViewById(R.id.prix);
        TextView type = findViewById(R.id.type);
        TextView capacite = findViewById(R.id.capacite);
        ImageView img = findViewById(R.id.imgRestoPage);
        Button reservationButton = findViewById(R.id.reserverButton);

        // Afficher les informations du restaurant sélectionné dans les TextViews
        displayRestaurantInfo(selectedRestaurant, title, description, adresse, telephone, horaire, prix, type, capacite, img, reservationButton);

        // Récupérer la référence au bouton "retour"
        Button retourButton = findViewById(R.id.retour);

        // Définir un écouteur de clic sur le bouton "retour"
        retourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Terminer l'activité actuelle pour revenir à la liste des restaurants
                finish();
            }
        });
        Button reviewButton = findViewById(R.id.avis);
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RestaurantPage.this, ReviewActivity.class);
                intent.putExtra("restaurantId", selectedRestaurant.getId());
                startActivity(intent);
            }
        });
        reservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Créer une intention pour démarrer l'activité de réservation
                Intent intent = new Intent(RestaurantPage.this, ReservationActivity.class);
                intent.putExtra("restaurantId", selectedRestaurant.getId());
                startActivity(intent);
            }
        });

    }

    private void displayRestaurantInfo(Restaurant restaurant, TextView title, TextView description, TextView adresse,
                                       TextView telephone, TextView horaire, TextView prix, TextView type,
                                       TextView capacite, ImageView img,Button reservationButton ) {
        // Récupérer les informations du restaurant sélectionné
        String restaurantName = restaurant.getTitle();
        String restaurantImg = restaurant.getImg();
        String restaurantType = restaurant.getType();
        String restaurantAddress = restaurant.getAddress();
        Double restaurantRating = restaurant.getRating();
        Boolean restaurantReservation = restaurant.getReservation();
        Date restaurantOpening = restaurant.getOpening();
        String restaurantDescription = restaurant.getDescription();
        String restaurantPhone = restaurant.getTel();
        String restaurantPrice = restaurant.getPrixMoy();
        String restaurantCapacity = restaurant.getCapacity();
        com.google.firebase.firestore.GeoPoint restaurantLocation = restaurant.getLocation(); // Récupérer la localisation du restaurant

        // Afficher les informations du restaurant sélectionné dans les TextViews
        title.setText(restaurantName);
        description.setText(restaurantDescription);
        adresse.setText(restaurantAddress);
        String existingTextTelephone = telephone.getText().toString();
        String newTextTelephone = existingTextTelephone + " " + restaurantPhone;
        telephone.setText(newTextTelephone);
        DateFormat dateFormat = new SimpleDateFormat("EEE, MMM d", Locale.US);
        String formattedOpening = dateFormat.format(restaurantOpening);
        String existingTextHoraire = horaire.getText().toString();
        String newTextHoraire = existingTextHoraire + " " + formattedOpening;
        horaire.setText(newTextHoraire);
        // Convertir la chaîne de caractères Base64 en tableau de bytes
        byte[] imageBytes = Base64.decode(restaurantImg, Base64.DEFAULT);

        // Charger l'image à partir du tableau de bytes à l'aide de Glide
        Glide.with(this).load(imageBytes).into(img);

        String existingTextPrix = prix.getText().toString();
        String newTextPrix = existingTextPrix + " " + restaurantPrice + " euros";
        prix.setText(newTextPrix);

        String existingTextType = type.getText().toString();
        String newTextType = existingTextType + " " + restaurantType;
        type.setText(newTextType);

        String existingTextCapacity = capacite.getText().toString();
        String newTextCapacity = existingTextCapacity + " " + restaurantCapacity;
        capacite.setText(newTextCapacity);

        if (restaurantLocation != null) {
            // Extraire les coordonnées de latitude et de longitude
            double latitude = restaurantLocation.getLatitude();
            double longitude = restaurantLocation.getLongitude();

            // Créer un marqueur pour le restaurant
            Marker restaurantMarker = new Marker(mapView);
            restaurantMarker.setPosition(new GeoPoint(latitude, longitude));
            restaurantMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            restaurantMarker.setTitle("Restaurant");
            mapView.getOverlays().add(restaurantMarker);

            // Centrer la carte sur le restaurant avec un niveau de zoom approprié
            mapView.getController().setCenter(new GeoPoint(latitude, longitude));
            mapView.getController().setZoom(12); // Niveau de zoom 12
        }
        // Vérifier si les réservations sont autorisées pour le restaurant sélectionné
        if (restaurantReservation) {
            // Les réservations sont autorisées, donc le bouton "réserver" est activé
            reservationButton.setEnabled(true);
        } else {
            // Les réservations ne sont pas autorisées, donc le bouton "réserver" est désactivé
            reservationButton.setEnabled(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        ;
    }
}