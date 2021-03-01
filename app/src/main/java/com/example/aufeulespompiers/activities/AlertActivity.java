package com.example.aufeulespompiers.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.aufeulespompiers.R;
import com.example.aufeulespompiers.Services.FirestoreService;
import com.example.aufeulespompiers.interfaces.OnAlertByIdReceivedListener;
import com.example.aufeulespompiers.model.Statement;

import java.text.SimpleDateFormat;

public class AlertActivity extends AppCompatActivity {

    private static final String TAG = "AlertActivity";
    private FirestoreService firestoreService;
    private Statement statement;

    private TextView sensor_title;
    private TextView statement_hour;
    private TextView temperature;
    private TextView latitude_example;
    private TextView longitude_example;
    private TextView someone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        sensor_title = findViewById(R.id.sensor_title);
        statement_hour = findViewById(R.id.statement_hour);
        temperature = findViewById(R.id.temperature);
        latitude_example = findViewById(R.id.latitude_example);
        longitude_example = findViewById(R.id.longitude_example);
        someone = findViewById(R.id.someone);

        firestoreService = new FirestoreService();
        Intent intent = getIntent();
        String id = intent.getStringExtra("statementId");
        firestoreService.getAlertById(id, new OnAlertByIdReceivedListener() {
            @Override
            public void onAlertByIdListReceived(Statement result) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                sensor_title.setText(result.getBeacon());
                statement_hour.setText(formatter.format(result.getDate().toDate()));
                temperature.setText(String.valueOf(result.getTemp()) );
                latitude_example.setText(String.valueOf(result.getPosition().getLatitude()));
                longitude_example.setText(String.valueOf(result.getPosition().getLongitude()));
            }
        });

    }

    public void backToMap(View view) {
        onBackPressed();
    }
}