package com.example.aufeulespompiers.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class Statement {

    private String id;
    private String beacon;
    private Timestamp date;
    private GeoPoint position;
    private Boolean resolve;
    private float temp;

    public Statement(String id, String beacon, Timestamp date, GeoPoint position, Boolean resolve, float temp) {
        this.id = id;
        this.beacon = beacon;
        this.date = date;
        this.position = position;
        this.resolve = resolve;
        this.temp = temp;
    }

    public Statement() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBeacon() {
        return beacon;
    }

    public void setBeacon(String beacon) {
        this.beacon = beacon;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Boolean getResolve() {
        return resolve;
    }

    public void setResolve(Boolean resolve) {
        this.resolve = resolve;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public GeoPoint getPosition() {
        return position;
    }

    public void setPosition(GeoPoint position) {
        this.position = position;
    }
}
