package com.example.miniprojet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextClock;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReservationActivity extends AppCompatActivity {

    EditText reservationText;
    EditText nombrePersonne;
    CalendarView dateReservation;
    TimePicker heureReservation;
    Button submitButton, exitButton;

    FirebaseFirestore db;
    String restaurantId;

    String formattedDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        reservationText = findViewById(R.id.reservation_text);
        dateReservation = findViewById(R.id.date_reservation);
        heureReservation = findViewById(R.id.heure_reservation);
        nombrePersonne = findViewById(R.id.nombre); // Initialisation du champ pour le nombre de personnes
        submitButton = findViewById(R.id.submit_reservation_button);
        exitButton = findViewById(R.id.exitButton);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        restaurantId = intent.getStringExtra("restaurantId"); // Utilisation de la variable de classe restaurantId

        dateReservation.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Convertir la date sélectionnée en une chaîne lisible
                formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);

                // Afficher la date sélectionnée dans la console
                Log.d("ReservationActivity", "Date sélectionnée: " + formattedDate);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmReservation();
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void confirmReservation() {
        String name = reservationText.getText().toString().trim();
        String numberOfPeople = nombrePersonne.getText().toString().trim();
        // Obtenez la date sélectionnée dans le calendrier


        // Obtenez l'heure sélectionnée dans le TimePicker
        int hour = heureReservation.getHour();
        int minute = heureReservation.getMinute();

        // Convertir l'heure et les minutes en une chaîne lisible
        String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);

        if (name.isEmpty() || selectedTime.isEmpty() || numberOfPeople.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (restaurantId == null) {
            Toast.makeText(this, "Erreur: ID du restaurant non disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convertir la date sélectionnée en une chaîne lisible
        //String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month, year);
        //Log.d("ReservationActivity", "Date sélectionnée: " + formattedDate);

        // Créer un objet pour stocker les détails de la réservation
        Map<String, Object> reservationData = new HashMap<>();
        reservationData.put("name", name);
        reservationData.put("nombre", numberOfPeople);
        reservationData.put("date", formattedDate); // Ajouter la date
        reservationData.put("time", selectedTime);

        // Ajouter la réservation à la collection appropriée dans Firestore
        db.collection("restaurant").document(restaurantId).collection("reservation")
                .add(reservationData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(ReservationActivity.this, "Réservation confirmée avec succès!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ReservationActivity.this, "Échec de la réservation. Veuillez réessayer.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}