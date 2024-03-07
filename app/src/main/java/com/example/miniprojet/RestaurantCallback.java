package com.example.miniprojet;

import com.example.miniprojet.model.Restaurant;

import java.util.List;

public interface RestaurantCallback {
    void onCallback(List<Restaurant> restaurants);
}
