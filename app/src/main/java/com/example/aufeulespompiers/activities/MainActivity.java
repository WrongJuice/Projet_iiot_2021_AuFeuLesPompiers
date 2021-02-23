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
import android.widget.TextView;

import com.example.aufeulespompiers.R;
import com.example.aufeulespompiers.Service.DataManager;
import com.example.aufeulespompiers.adapters.AlertAdapter;
import com.example.aufeulespompiers.model.Alert;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RelativeLayout alertView;
    RelativeLayout infoView;
    ListView alertsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alertView = findViewById(R.id.alert_view);
        infoView = findViewById(R.id.info_view);
        alertsList = findViewById(R.id.alert_list);

        DataManager.generateFakesAlerts();
        ArrayList<Alert> alerts = DataManager.getAlerts();
        AlertAdapter alertAdapter = new AlertAdapter(this, alerts);
        alertsList.setAdapter(alertAdapter);

        if (alerts.isEmpty()) {
            System.out.println("LOL");
            alertsList.setVisibility(View.GONE);
            findViewById(R.id.no_alert).setVisibility(View.VISIBLE);
        } else {

            ViewGroup.LayoutParams params = alertView.getLayoutParams();
            alertView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    alertView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    //height is ready
                    if (alertView.getHeight() > pxToDp(400)) {
                        params.height = pxToDp(400);
                        alertView.setLayoutParams(params);
                    }
                }
            });
        }

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