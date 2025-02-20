package com.example.looking4fight.data.model;

public class Event {
    private String title;
    private String date;
    private String location;
    private String description;

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
    public String getLocation() { return location; }
    public String getDescription() { return description; }
}
