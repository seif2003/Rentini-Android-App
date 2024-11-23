package com.example.rentini.models;

public class Property {
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
    }

    // Add new getters/setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getRooms() { return rooms; }
    public void setRooms(int rooms) { this.rooms = rooms; }

    public double getSurface() { return surface; }
    public void setSurface(double surface) { this.surface = surface; }

    // Existing getters/setters remain the same
}