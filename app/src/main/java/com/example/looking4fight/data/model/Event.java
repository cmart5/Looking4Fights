package com.example.looking4fight.data.model;

import com.google.firebase.firestore.GeoPoint;

public class Event {
    private String title;
    private String date;
    private String location;
    private String description;

    private GeoPoint locationPoint;

    public Event() {
        // Empty constructor required for Firestore
    }

    public Event(String title, String date, String location, String description) {
        this.title = title;
        this.date = date;
        this.location = location;
        this.description = description;
    }

    public String getTitle() { return title; }
    public String getDate() { return date; }
    //public String getLocation() { return location; } //removed to fix error
    public String getDescription() { return description; }

    public GeoPoint getLocation() { return locationPoint; } //added to fix error

    public void setLocation(GeoPoint locationPoint) { this.locationPoint = locationPoint; } //added to fix error
}
