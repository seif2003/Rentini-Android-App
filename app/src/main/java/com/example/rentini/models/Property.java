package com.example.rentini.models;

import java.io.Serializable;

public class Property implements Serializable {
    private String id;
    private String title;
    private String description;
    private String type; // day/week/month
    private double price;
    private int rooms;
    private double surface; // in square meters
    private double latitude;
    private double longitude;
    private String userId;
    private boolean hasWifi;
    private boolean hasParking;
    private boolean hasKitchen;
    private boolean hasAirConditioning;
    private boolean hasFurnished;
    private boolean saved;

    // Default constructor
    public Property() {}

    // Updated constructor
    public Property(String title, String description, String type, double price, 
                   int rooms, double surface, double latitude, double longitude, 
                   String userId, boolean hasWifi, boolean hasParking, 
                   boolean hasKitchen, boolean hasAirConditioning, 
                   boolean hasFurnished) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.price = price;
        this.rooms = rooms;
        this.surface = surface;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userId = userId;
        this.hasWifi = hasWifi;
        this.hasParking = hasParking;
        this.hasKitchen = hasKitchen;
        this.hasAirConditioning = hasAirConditioning;
        this.hasFurnished = hasFurnished;
        this.saved = false; // Initialize saved as false
    }

    // Getters and Setters for all attributes
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public double getSurface() {
        return surface;
    }

    public void setSurface(double surface) {
        this.surface = surface;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isHasWifi() {
        return hasWifi;
    }

    public void setHasWifi(boolean hasWifi) {
        this.hasWifi = hasWifi;
    }

    public boolean isHasParking() {
        return hasParking;
    }

    public void setHasParking(boolean hasParking) {
        this.hasParking = hasParking;
    }

    public boolean isHasKitchen() {
        return hasKitchen;
    }

    public void setHasKitchen(boolean hasKitchen) {
        this.hasKitchen = hasKitchen;
    }

    public boolean isHasAirConditioning() {
        return hasAirConditioning;
    }

    public void setHasAirConditioning(boolean hasAirConditioning) {
        this.hasAirConditioning = hasAirConditioning;
    }

    public boolean isHasFurnished() {
        return hasFurnished;
    }

    public void setHasFurnished(boolean hasFurnished) {
        this.hasFurnished = hasFurnished;
    }
}

