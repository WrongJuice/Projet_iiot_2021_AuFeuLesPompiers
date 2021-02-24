package com.example.aufeulespompiers.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.aufeulespompiers.R;
import com.example.aufeulespompiers.Service.DataManager;
import com.example.aufeulespompiers.adapters.AlertAdapter;
import com.example.aufeulespompiers.model.Alert;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RelativeLayout alertView;
    RelativeLayout infoView;
    RelativeLayout sensorView;
    ListView alertsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alertView = findViewById(R.id.alert_view);
        infoView = findViewById(R.id.info_view);
        alertsList = findViewById(R.id.alert_list);
        sensorView = findViewById(R.id.sensor_view);

        DataManager.generateFakesData();
        ArrayList<Alert> alerts = DataManager.getAlerts();
        AlertAdapter alertAdapter = new AlertAdapter(this, alerts);
        alertsList.setAdapter(alertAdapter);

        if (alerts.isEmpty()) {
            alertsList.setVisibility(View.GONE);
            findViewById(R.id.no_alert).setVisibility(View.VISIBLE);
        } else {
            ViewGroup.LayoutParams params = alertsList.getLayoutParams();
            alertsList.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    alertsList.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    //height is ready
                    params.height = Math.min(pxToDp(60)*alerts.size(), pxToDp(200));
                    alertsList.setLayoutParams(params);
                }
            });
        }

        sensorView.setOnClickListener(view -> {
            /*Intent intent = new Intent(this, StatementsListActivity.class);
            startActivity(intent);*/
        });

        alertView.setOnClickListener(view -> {
            Intent intent = new Intent(this, AlertsListActivity.class);
            startActivity(intent);
        });

        infoView.setOnClickListener(view -> {
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
        });

    }

    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int)((px * displayMetrics.density) + 0.5);
    }

}