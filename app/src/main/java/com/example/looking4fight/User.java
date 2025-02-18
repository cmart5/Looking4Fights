package com.example.looking4fight;

import java.time.LocalDate;

public class User {
    private String name;
    private int id;
    private LocalDate localDate;

    public User(int id, String name, LocalDate localDate) {
        this.id = id;
        this.name = name;
        this.localDate = localDate;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public LocalDate getDatePosted() {
        return localDate;
    }
}