package com.example.aufeulespompiers.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

public class SensorStatement {

    private String title;
    private Calendar creationDate;
    private double temperature;

    public SensorStatement(String title, Calendar creationDate, LatLng coordinate, double temperature) {
        this.title = title;
        this.creationDate = creationDate;
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

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

}
