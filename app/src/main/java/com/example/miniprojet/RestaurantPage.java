package com.example.miniprojet;

import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.miniprojet.model.Restaurant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class RestaurantPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resto_page);

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

        // Vérifier si les réservations sont autorisées pour le restaurant sélectionné
        if (restaurantReservation) {
            // Les réservations sont autorisées, donc le bouton "réserver" est activé
            reservationButton.setEnabled(true);
        } else {
            // Les réservations ne sont pas autorisées, donc le bouton "réserver" est désactivé
            reservationButton.setEnabled(false);
        }
    }
}