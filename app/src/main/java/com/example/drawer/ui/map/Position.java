package com.example.drawer.ui.map;
public class Position {
    private int id;
    private double latitude;
    private double longitude;
    private String date;
    private String imei;

    // Constructor
    public Position(double latitude, double longitude, String date, String imei) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.imei = imei;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}