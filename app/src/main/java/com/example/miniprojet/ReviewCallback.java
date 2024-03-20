package com.example.miniprojet;

import com.example.miniprojet.model.Restaurant;
import com.example.miniprojet.model.Review;

import java.util.List;

public interface ReviewCallback {
    void onCallback(List<Review> restaurants);

}
