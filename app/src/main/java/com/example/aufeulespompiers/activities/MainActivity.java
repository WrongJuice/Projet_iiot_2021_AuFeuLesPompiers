package com.example.aufeulespompiers.activities;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.aufeulespompiers.R;
import com.example.aufeulespompiers.Services.DataManager;
import com.example.aufeulespompiers.Services.FirestoreService;
import com.example.aufeulespompiers.adapters.AlertAdapter;
import com.example.aufeulespompiers.interfaces.OnAlertReceivedListener;
import com.example.aufeulespompiers.interfaces.OnStatementReceivedListener;
import com.example.aufeulespompiers.model.Alert;
import com.example.aufeulespompiers.model.Statement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.aufeulespompiers.R;
import com.example.aufeulespompiers.fragments.MapFragment;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PermissionsListener {
    /*
    RelativeLayout alertView;
    RelativeLayout infoView;
    RelativeLayout sensorView;
    ListView alertsList;*/

    // distance part
    private PermissionsManager permissionsManager;
    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Subscribe to list of notified
        FirebaseMessaging.getInstance().subscribeToTopic("pompier");

        /*
        alertView = findViewById(R.id.alert_view);
        infoView = findViewById(R.id.info_view);
        alertsList = findViewById(R.id.alert_list);
        sensorView = findViewById(R.id.sensor_view);

        DataManager.generateFakesData();
        ArrayList<Alert> alerts = DataManager.getAlerts();
        AlertAdapter alertAdapter = new AlertAdapter(this, alerts);
        alertsList.setAdapter(alertAdapter);*/

        /*if (alerts.isEmpty()) {
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
            // Intent intent = new Intent(this, StatementsListActivity.class);
            // startActivity(intent);
        });

        alertView.setOnClickListener(view -> {
            Intent intent = new Intent(this, AlertsListActivity.class);
            startActivity(intent);
        });

        infoView.setOnClickListener(view -> {
            Intent intent = new Intent(this, InfoActivity.class);
            startActivity(intent);
        });
        */

        FragmentTransaction ft = getSupportFragmentManager()
                .beginTransaction();
        mapFragment = new MapFragment();
        ft.replace(R.id.map_container, mapFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapFragment.getMapView().onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapFragment.getMapView().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapFragment.getMapView().onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapFragment.getMapView().onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapFragment.getMapView().onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapFragment.preventLeak();
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        mapFragment.treatOnExplanationNeeded();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        mapFragment.treatOnPermissionResult(granted);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /*
    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int)((px * displayMetrics.density) + 0.5);
    }
    */

}