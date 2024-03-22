package com.example.miniprojet.model;

import java.util.UUID;

public class Reservation {

    private String id;
    private String name;
    private String numberOfPeople;
    private String date;
    private String time;

    public Reservation() {}

    public Reservation(String name, String numberOfPeople, String date, String time) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.numberOfPeople = numberOfPeople;
        this.date = date;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(String numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
