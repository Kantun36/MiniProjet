package com.example.miniprojet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private final FirebaseFirestore db;

    MainActivity(FirebaseFirestore db) {
        this.db = db;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}