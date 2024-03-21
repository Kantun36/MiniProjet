package com.example.miniprojet.model;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.StorageReference;
import com.google.type.DateTime;

import java.util.Date;

@SuppressLint("ParcelCreator")
public class Restaurant implements Parcelable {
    private String id;
    private String title;
    private String type;
    private String address;
    private String description;
    private String prixMoy;
    private String tel;
    private String capacity;
    private String img;
    private Double rating;
    private Boolean reservation;
    private Date opening;

    private GeoPoint location;

    private Double latitude;

    private Double longitude;

    protected Restaurant(Parcel in) {
        id = in.readString();
        title = in.readString();
        type = in.readString();
        address = in.readString();
        description = in.readString();
        prixMoy = in.readString();
        tel = in.readString();
        capacity = in.readString();
        img = in.readString();
        rating = in.readDouble();
        reservation = in.readByte() != 0;
        long openingMillis = in.readLong();
        opening = new Date(openingMillis);
        latitude = in.readDouble();
        longitude = in.readDouble();
    }
    public Restaurant(String id,String title, String type, String address,String description,String prixMoy, String tel,String capacity, String img, Double rating, Boolean reservation, Date opening, GeoPoint location){
        this.id = id;
        this.title = title;
        this.address = address;
        this.description = description;
        this.prixMoy = prixMoy;
        this.tel = tel;
        this.capacity = capacity;
        this.type = type;
        this.img = img;
        this.rating = rating;
        this.reservation = reservation;
        this.opening = opening;
            this.location = location;
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();


    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };
    public String getImg() {
        return img;
    }

    public String getId() {
        return id;
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

    public String getDescription() {
        return description;
    }

    public String getPrixMoy() {
        return prixMoy;
    }

    public String getTel() {
        return tel;
    }

    public String getCapacity() {
        return capacity;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressLint("NewApi")
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(type);
        dest.writeString(address);
        dest.writeString(description);
        dest.writeString(prixMoy);
        dest.writeString(tel);
        dest.writeString(capacity);
        dest.writeString(img);
        dest.writeDouble(rating);
        dest.writeByte((byte) (reservation ? 1 : 0));
        dest.writeLong(opening.getTime());

            dest.writeDouble(latitude);
            dest.writeDouble(longitude);
            //Log.d("LocationInfo2", "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude());

    }
}

