package com.example.aufeulespompiers.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private Button treat_alert;
    //============================================================================================
    // iBeacon variables
    //============================================================================================
    private BluetoothManager btManager;
    private BluetoothAdapter btAdapter;
    private Handler scanHandler = new Handler();
    private int scan_interval_ms = 5000;
    private boolean isScanning = false;

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
        treat_alert = findViewById(R.id.treat_alert);
        //============================================================================================
        // iBeacon Init
        //============================================================================================
        btManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();

        scanHandler.post(scanRunnable);

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
                someone.setText(String.valueOf(result.getAssignedTo()));
            }
        });

    }

    private Runnable scanRunnable = new Runnable() {
        @Override
        public void run() {
            if(isScanning){
                if (btAdapter != null){
                    btAdapter.stopLeScan(leScanCallback);
                }
            }
            else{
                if(btAdapter != null){
                    btAdapter.startLeScan(leScanCallback);
                }
            }

            isScanning = !isScanning;
            scanHandler.postDelayed(this, scan_interval_ms);
        }
    };

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            int startByte = 2;
            boolean patternFound = false;
            while (startByte <= 5){
                if ( ((int) scanRecord[startByte + 2] & 0xff)  == 0x02 && ((int) scanRecord[startByte + 3] & 0xff) == 0x15){
                    patternFound = true;
                    break;
                }
                startByte++;
            }

            if (patternFound){
                byte[] uuidBytes = new byte[16];
                System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
                String hexString = bytesToHex(uuidBytes);

                String uuid = hexString.substring(0,8) + "-" +
                        hexString.substring(8,12) + "-" +
                        hexString.substring(12,16) + "-" +
                        hexString.substring(16,20) + "-" +
                        hexString.substring(20,32);

                final int major = (scanRecord[startByte + 20] & 0xff) * 0x100 + (scanRecord[startByte + 21] & 0xff);
                final int minor = (scanRecord[startByte + 22] & 0xff) * 0x100 + (scanRecord[startByte + 23] & 0xff);

                // ne prend que les beacons à proximité direct du device
                Log.d(TAG, "onLeScan: "+ rssi);
                if (rssi >= -40){
                    treat_alert.setEnabled(true);
                }

            }
        }
    };

    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes){
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++){
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public void backToMap(View view) {
        onBackPressed();
    }
}