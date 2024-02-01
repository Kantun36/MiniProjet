package com.example.miniprojet;

import com.google.firebase.Timestamp;
import com.google.type.DateTime;

import java.util.Date;

public class Restaurant {
    private String title;
    private String type;
    private String address;
    private int imgId;
    private Double rating;
    private Boolean reservation;
    private Timestamp opening;

    public Restaurant(String title, String type, String address, int imgId, Double rating, Boolean reservation, Timestamp opening) {
        this.title = title;
        this.address = address;
        this.type = type;
        this.imgId = imgId;
        this.rating = rating;
        this.reservation = reservation;
        this.opening = opening;
    }
    public int getImgId() {
        return imgId;
    }

    public Boolean getReservation() {
        return reservation;
    }

    public Timestamp getOpening() {
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

