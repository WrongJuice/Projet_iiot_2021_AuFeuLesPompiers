package com.example.aufeulespompiers.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aufeulespompiers.R;
import com.example.aufeulespompiers.Services.AuthenticationService;
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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PermissionsListener {
    private static final String TAG = "MainActivity";

    private PermissionsManager permissionsManager;
    private MapFragment mapFragment;
    AuthenticationService auth = AuthenticationService.getInstance();

    //============================================================================================
    // Variable NFC
    //============================================================================================
    IntentFilter[] filters;
    String[][] techs;
    PendingIntent pendingIntent;
    NfcAdapter adapter;
    ArrayList<String> userCertified;
    TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Subscribe to list of notified
        FirebaseMessaging.getInstance().subscribeToTopic("pompier");
        Log.d(TAG, "onCreate: COUCOU");

        //============================================================================================
        // Init NFC
        //============================================================================================
        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter mifare = new IntentFilter((NfcAdapter.ACTION_TECH_DISCOVERED));
        filters = new IntentFilter[] { mifare };
        techs = new String[][] { new String[] {NfcA.class.getName()}};
        adapter = NfcAdapter.getDefaultAdapter(this);
        username = findViewById(R.id.username);

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

    //============================================================================================
    // Début recherche NFC
    //============================================================================================
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        byte[] id = tag.getId();
        ByteBuffer wrapped = ByteBuffer.wrap(id);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        int signedInt = wrapped.getInt();
        long number = signedInt & 0xfffffffl;
        ident(number);
    }

    public void ident (long number) {
        Log.d(TAG, "number: "+number);
        if(auth.getUserAutrorized().contains(number)){
            username.setText("user N°" + number);
            auth.setCurrentUser(number);
        } else {
            Toast toast = Toast.makeText(this, "Connection refusée", Toast.LENGTH_LONG);
            toast.show();
        }
    }
    //============================================================================================
    // Fin recherche NFC
    //============================================================================================

    @Override
    protected void onStart() {
        super.onStart();
        mapFragment.getMapView().onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.enableForegroundDispatch(this, pendingIntent, filters, techs);
        mapFragment.getMapView().onResume();
        reloadMap();
    }

    @Override
    protected void onPause() {
        super.onPause();
        adapter.disableForegroundDispatch(this);
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

    public void reloadMap(){
        FragmentTransaction ft = getSupportFragmentManager()
                .beginTransaction();
        mapFragment = new MapFragment();
        ft.replace(R.id.map_container, mapFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
}