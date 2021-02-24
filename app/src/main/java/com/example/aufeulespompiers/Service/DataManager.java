package com.example.aufeulespompiers.Service;

import com.example.aufeulespompiers.model.Alert;
import com.example.aufeulespompiers.model.SensorStatement;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;

public class DataManager {

    private static ArrayList<SensorStatement> sensorStatements = new ArrayList<>();
    private static ArrayList<Alert> alerts = new ArrayList<>();

    public static void generateFakesData() {
        alerts.add(new Alert("Forêt secteur 02C", Calendar.getInstance(), new LatLng(46.141282, -1.149225), 43.5));

        alerts.add(new Alert("Forêt secteur 02C", Calendar.getInstance(), new LatLng(46.141282, -1.149225), 43.5));

        alerts.add(new Alert("Forêt secteur 02C", Calendar.getInstance(), new LatLng(46.141282, -1.149225), 43.5));

        alerts.add(new Alert("Forêt secteur 02C", Calendar.getInstance(), new LatLng(46.141282, -1.149225), 43.5));

        sensorStatements.add(new SensorStatement("Forêt secteur 02C", Calendar.getInstance(), new LatLng(46.141282, -1.149225), 43.5));
        sensorStatements.add(new SensorStatement("Forêt secteur 02C", Calendar.getInstance(), new LatLng(46.141282, -1.149225), 43.5));
        sensorStatements.add(new SensorStatement("Forêt secteur 02C", Calendar.getInstance(), new LatLng(46.141282, -1.149225), 43.5));
        sensorStatements.add(new SensorStatement("Forêt secteur 02C", Calendar.getInstance(), new LatLng(46.141282, -1.149225), 43.5));
        sensorStatements.add(new SensorStatement("Forêt secteur 02C", Calendar.getInstance(), new LatLng(46.141282, -1.149225), 43.5));
        sensorStatements.add(new SensorStatement("Forêt secteur 02C", Calendar.getInstance(), new LatLng(46.141282, -1.149225), 43.5));
        sensorStatements.add(new SensorStatement("Forêt secteur 02C", Calendar.getInstance(), new LatLng(46.141282, -1.149225), 43.5));
        sensorStatements.add(new SensorStatement("Forêt secteur 02C", Calendar.getInstance(), new LatLng(46.141282, -1.149225), 43.5));
        sensorStatements.add(new SensorStatement("Forêt secteur 02C", Calendar.getInstance(), new LatLng(46.141282, -1.149225), 43.5));
        sensorStatements.add(new SensorStatement("Forêt secteur 02C", Calendar.getInstance(), new LatLng(46.141282, -1.149225), 43.5));
        sensorStatements.add(new SensorStatement("Forêt secteur 02C", Calendar.getInstance(), new LatLng(46.141282, -1.149225), 43.5));
    }

    public static ArrayList<SensorStatement> getSensorStatements() {
        return sensorStatements;
    }

    public static ArrayList<Alert> getAlerts() {
        return alerts;
    }

    public static void setAlerts(ArrayList<Alert> alerts) {
        DataManager.alerts = alerts;
    }
}
