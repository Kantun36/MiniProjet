package com.example.miniprojet.model;

import com.google.firebase.Timestamp;
import com.google.firebase.storage.StorageReference;
import com.google.type.DateTime;

import java.util.Date;

public class Restaurant {
    private String title;
    private String type;
    private String address;
    private StorageReference img;
    private Double rating;
    private Boolean reservation;
    private Date opening;

    public Restaurant(String title, String type, String address, StorageReference img, Double rating, Boolean reservation, Date opening) {
        this.title = title;
        this.address = address;
        this.type = type;
        this.img = img;
        this.rating = rating;
        this.reservation = reservation;
        this.opening = opening;
    }
    public StorageReference getImg() {
        return img;
    }

    public Boolean getReservation() {
        return reservation;
    }

    public Date getOpening() {
        return opening;
    }

    public Double getRating() {
        return rating;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getAddress() {
        return address;
    }
}

