package com.example.aufeulespompiers.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

public class Alert {

    private String title;
    private Calendar creationDate;
    private LatLng coordinate; // if we put a map in the future
    private double temperature;

    public Alert(String title, Calendar creationDate, LatLng coordinate, double temperature) {
        this.title = title;
        this.creationDate = creationDate;
        this.coordinate = coordinate;
        this.temperature = temperature;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Calendar getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Calendar creationDate) {
        this.creationDate = creationDate;
    }

    public LatLng getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(LatLng coordinate) {
        this.coordinate = coordinate;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
