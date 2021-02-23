package com.example.aufeulespompiers.Service;

import com.example.aufeulespompiers.model.Alert;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;

public class DataManager {
    private static ArrayList<Alert> alerts = new ArrayList<>();

    public static ArrayList<Alert> getAlerts() {
        return alerts;
    }

    public static void setAlerts(ArrayList<Alert> alerts) {
        DataManager.alerts = alerts;
    }

    public static void generateFakesAlerts() {
        alerts.add(new Alert("Forêt secteur 02C", Calendar.getInstance(), new LatLng(46.141282, -1.149225), 43.5));

        alerts.add(new Alert("Forêt secteur 02C", Calendar.getInstance(), new LatLng(46.141282, -1.149225), 43.5));

        alerts.add(new Alert("Forêt secteur 02C", Calendar.getInstance(), new LatLng(46.141282, -1.149225), 43.5));

        alerts.add(new Alert("Forêt secteur 02C", Calendar.getInstance(), new LatLng(46.141282, -1.149225), 43.5));
    }
}
