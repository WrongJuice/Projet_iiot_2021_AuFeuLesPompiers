package com.example.aufeulespompiers.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    RelativeLayout alertView;
    RelativeLayout infoView;
    RelativeLayout sensorView;
    ListView alertsList;
    private FirestoreService firestoreService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Subscribe to list of notified
        FirebaseMessaging.getInstance().subscribeToTopic("pompier");
        alertView = findViewById(R.id.alert_view);
        infoView = findViewById(R.id.info_view);
        alertsList = findViewById(R.id.alert_list);
        sensorView = findViewById(R.id.sensor_view);

        DataManager.generateFakesData();
        ArrayList<Alert> alerts = DataManager.getAlerts();
        AlertAdapter alertAdapter = new AlertAdapter(this, alerts);
        alertsList.setAdapter(alertAdapter);

        firestoreService = new FirestoreService();
        firestoreService.getStatements(new OnStatementReceivedListener() {
            @Override
            public void onStatementListReceived(ArrayList<Statement> result) {
                Log.d(TAG, "onStatementListReceived: " + result.toString());
                for(Statement tempStatement : result){
                    tempStatement.setResolve(false);
                    firestoreService.modifyStatmentResolve(tempStatement);
                }
            }
        });
        firestoreService.getAlerts(new OnAlertReceivedListener() {
            @Override
            public void onAlertListReceived(ArrayList<Statement> result) {
                Log.d(TAG, "onAlertListReceived: " + result.toString());
            }
        });

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