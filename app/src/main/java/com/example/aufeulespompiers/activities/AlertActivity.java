package com.example.aufeulespompiers.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.example.aufeulespompiers.R;
import com.example.aufeulespompiers.Services.AuthenticationService;
import com.example.aufeulespompiers.Services.FirestoreService;
import com.example.aufeulespompiers.interfaces.OnAlertByIdReceivedListener;
import com.example.aufeulespompiers.model.Statement;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class AlertActivity extends Activity implements SurfaceHolder.Callback {

    private static final String TAG = "AlertActivity";
    private static final int PHOTO_RESULT = 123456;
    private FirestoreService firestoreService;
    private Statement statement;
    private Camera mCamera = null;

    private TextView statement_status;
    private TextView sensor_title;
    private TextView statement_hour;
    private TextView temperature;
    private TextView latitude_example;
    private TextView longitude_example;
    private TextView someone;
    private Button treat_alert;
    private SurfaceView surfaceView;
    AuthenticationService auth = AuthenticationService.getInstance();
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

        statement_status = findViewById(R.id.statement_status);
        sensor_title = findViewById(R.id.sensor_title);
        statement_hour = findViewById(R.id.statement_hour);
        temperature = findViewById(R.id.temperature);
        latitude_example = findViewById(R.id.latitude_example);
        longitude_example = findViewById(R.id.longitude_example);
        someone = findViewById(R.id.someone);
        treat_alert = findViewById(R.id.treat_alert);
        surfaceView = findViewById(R.id.surfaceView3);

        statement = new Statement();

        //============================================================================================
        // iBeacon Init
        //============================================================================================
        btManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        scanHandler.post(scanRunnable);

        SurfaceHolder holder = surfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // On déclare que la classe actuelle gérera les callbacks
        holder.addCallback(this);

        firestoreService = new FirestoreService();
        Intent intent = getIntent();
        String id = intent.getStringExtra("statementId");
        firestoreService.getAlertById(id, new OnAlertByIdReceivedListener() {
            @Override
            public void onAlertByIdListReceived(Statement result) {
                statement = result;
                if (statement.getResolve()){
                    statement_status.setBackgroundColor(Color.GREEN);
                    statement_status.setText("Alerte traité");
                    treat_alert.setVisibility(View.GONE);
                }
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                sensor_title.setText(result.getBeacon());
                statement_hour.setText(formatter.format(result.getDate().toDate()));
                temperature.setText(String.valueOf(result.getTemp()) );
                latitude_example.setText(String.valueOf(result.getPosition().getLatitude()));
                longitude_example.setText(String.valueOf(result.getPosition().getLongitude()));
                someone.setText(String.valueOf(result.getAssignedTo()));

            }
        });
        treat_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resolveAlert();
            }
        });
    }
    //============================================================================================
    // Start beacon discovery
    //============================================================================================
    // Scan bluetooth devices every 5 seconds for 5 seconds
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
                Log.d(TAG, "onLeScan: " + statement.getAssignedTo());
                if (rssi >= -40 && auth.getCurrentUser() == statement.getAssignedTo()){
                    treat_alert.setEnabled(true);
                    treat_alert.setBackgroundColor(Color.GREEN);
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
    //============================================================================================
    // Stop beacon discovery
    //============================================================================================

    public void resolveAlert() {
        statement.setResolve(true);
        firestoreService.modifyStatmentResolve(statement);
        statement_status.setBackgroundColor(Color.GREEN);
        statement_status.setText("Alerte traité");
        treat_alert.setVisibility(View.GONE);
    }

    public void backToMap(View view) {
        onBackPressed();
    }

    @Override
    //Trigger when the surface is created
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            // set orientation camera
            mCamera.setDisplayOrientation(90);
            // start capture camera vision
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        mCamera.stopPreview();
    }

    @Override
    protected void onResume() {
        // When the activity restart, reopen the camera preview
        super.onResume();
        mCamera = Camera.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // When the activity is quit, stop the camera preview
        mCamera.release();
    }
}