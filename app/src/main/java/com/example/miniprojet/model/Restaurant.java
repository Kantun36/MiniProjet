package com.example.miniprojet.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.storage.StorageReference;
import com.google.type.DateTime;

import java.util.Date;

@SuppressLint("ParcelCreator")
public class Restaurant implements Parcelable {
    private String title;
    private String type;
    private String address;
    private StorageReference img;
    private Double rating;
    private Boolean reservation;
    private Date opening;

    protected Restaurant(Parcel in) {
        title = in.readString();
        type = in.readString();
        address = in.readString();
    }
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

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressLint("NewApi")
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(address);
        dest.writeString(type);
        dest.writeString(String.valueOf(img));
        dest.writeDouble(rating);
        dest.writeBoolean(reservation);
        dest.writeSerializable(opening);

    }
}

