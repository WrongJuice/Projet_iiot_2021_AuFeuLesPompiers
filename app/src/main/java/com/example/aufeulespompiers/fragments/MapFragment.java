package com.example.aufeulespompiers.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.aufeulespompiers.R;
import com.example.aufeulespompiers.Services.AuthenticationService;
import com.example.aufeulespompiers.Services.FirestoreService;
import com.example.aufeulespompiers.activities.AlertActivity;
import com.example.aufeulespompiers.activities.MainActivity;
import com.example.aufeulespompiers.model.Statement;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

import static android.os.Looper.getMainLooper;
import static com.google.android.gms.maps.model.BitmapDescriptorFactory.fromResource;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "MapFragment";

    // views
    private MapView mapView;
    private MapboxMap mapboxMap;
    private Button alertPage;

    View view;

    // private FragmentActivity context;

    // Variables needed to add the location engine
    private LocationEngine locationEngine;
    // Variables needed to listen to location updates
    private final MapFragment.MapFragmentLocationCallback callback =
            new MapFragment.MapFragmentLocationCallback(this);

    // Adjust private static final variables below to change the example's UI
    private static final String STYLE_URI = "mapbox://styles/mapbox/cjv6rzz4j3m4b1fqcchuxclhb";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Mapbox.getInstance(inflater.getContext(), getString(R.string.mapbox_access_token));
        view = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState); // prob with security/telemetry ??
        mapView.getMapAsync(this);

        alertPage = view.findViewById(R.id.take_alert);


        alertPage.setEnabled(true/*Replace by isAuth condition*/);


        return view;
    }

    public MapView getMapView() {
        return mapView;
    }

    public void preventLeak() {
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
        mapView.onDestroy();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(new Style.Builder().fromUri(STYLE_URI)
                    , this::enableLocationComponent);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());

        FirestoreService firestoreService = new FirestoreService();
        firestoreService.getStatements(result -> {
            for(Statement tempStatement : result){
                tempStatement.setResolve(false);
                firestoreService.modifyStatmentResolve(tempStatement);
            }
        });

        AuthenticationService auth = new AuthenticationService();

        // Create an Icon object for the marker to use
        IconFactory iconFactory = IconFactory.getInstance(Objects.requireNonNull(getActivity()));

        Icon iconInProgress = iconFactory.fromBitmap(BitmapFactory.decodeResource(
                getActivity().getResources(), R.drawable.ic_baseline_location_on_in_progress));

        Icon iconNotGood = iconFactory.fromBitmap(BitmapFactory.decodeResource(
                getActivity().getResources(), R.drawable.ic_baseline_location_on_not_good));

        Icon iconGood = iconFactory.fromBitmap(BitmapFactory.decodeResource(
                getActivity().getResources(), R.drawable.ic_baseline_location_on_good));

        firestoreService.getAlerts(result -> {
            Log.d(TAG, "onAlertListReceived: " + result.toString());

            for (Statement statement : result) {
                String snippet = "Heure : " + simpleDateFormat.format(statement.getDate().toDate())
                        + "\nTempérature : " + statement.getTemp() + "°C\nStatus : " + "Non attribué";
                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(statement.getPosition().getLatitude(),
                                statement.getPosition().getLongitude()))
                        .setSnippet(snippet)
                        .setIcon((statement.getAssignedTo() == 0)? iconNotGood : (statement.getResolve())? iconGood : iconInProgress)
                        .title(statement.getBeacon()));

                mapboxMap.setOnMarkerClickListener(marker -> {
                    for (Marker aMarker : mapboxMap.getMarkers()) aMarker.hideInfoWindow();
                    marker.showInfoWindow(mapboxMap, mapView);

                    mapboxMap.getUiSettings().setAllGesturesEnabled(false);

                    Statement anotherStatement = null;
                    for (Statement aStatement : result) {
                        if (marker.getPosition().getLatitude() == aStatement.getPosition().getLatitude() && marker.getPosition().getLongitude() == aStatement.getPosition().getLongitude())
                            anotherStatement = aStatement;
                    }

                    if (anotherStatement != null) {
                        if (anotherStatement.getAssignedTo() == 0) {
                            alertPage.setEnabled(auth.getId() != 0);
                            alertPage.setText("Je prends en charge l'alerte");
                            Statement finalStatement = anotherStatement;
                            alertPage.setOnClickListener(view1 -> {
                                finalStatement.setAssignedTo(auth.getId()); // need reload
                                String btnMessage = "Gérer l'alerte";
                                alertPage.setText(btnMessage);
                                marker.setIcon(iconInProgress);
                            });
                        } else if (anotherStatement.getResolve()) {
                            alertPage.setEnabled(false);
                            String btnMessage = "Traité par "+ anotherStatement.getAssignedTo();
                            alertPage.setText(btnMessage);
                        } else if (anotherStatement.getAssignedTo() == auth.getId()) {
                            alertPage.setEnabled(auth.getId() != 0);
                            String btnMessage = "Gérer l'alerte";
                            alertPage.setText(btnMessage);
                            Statement finalStatement1 = anotherStatement;
                            alertPage.setOnClickListener(view1 -> {
                                Intent intent = new Intent(getActivity(), AlertActivity.class);
                                intent.putExtra("statementId", finalStatement1.getId());
                                startActivity(intent);
                                marker.setIcon(iconInProgress);
                            });
                        } else {
                            alertPage.setEnabled(auth.getId() != 0);
                            String btnMessage = "Assigné à "+ auth.getId();
                            alertPage.setText(btnMessage);
                            Statement finalStatement2 = anotherStatement;
                            alertPage.setOnClickListener(view1 -> {
                                Intent intent = new Intent(getActivity(), AlertActivity.class);
                                intent.putExtra("statementId", finalStatement2.getId());
                                startActivity(intent);
                            });
                        }
                    }

                    return true;
                });



            }
        });

        mapView.setOnTouchListener((v, event) -> {
            for (Marker aMarker : mapboxMap.getMarkers()) aMarker.hideInfoWindow();
            mapboxMap.getUiSettings().setAllGesturesEnabled(true);
            v.onTouchEvent(event);
            alertPage.setEnabled(false);
            alertPage.setText("Sélectionner une alerte");
            return true;
        });

    }

    /**
     * Initialize the Maps SDK's LocationComponent
     */
    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {

        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(view.getContext())) {

            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Set the LocationComponent activation options
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(view.getContext(), loadedMapStyle)
                            .useDefaultLocationEngine(false)
                            .build();

            // Activate with the LocationComponentActivationOptions object
            locationComponent.activateLocationComponent(locationComponentActivationOptions);

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

            initLocationEngine();

        }
    }

    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */

    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(view.getContext());
        long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
        long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }

    public void treatOnExplanationNeeded() {
        Toast.makeText(view.getContext(), R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    public void treatOnPermissionResult(boolean granted) {
        if (granted)
            if (mapboxMap.getStyle() != null) {
                enableLocationComponent(mapboxMap.getStyle());
            }
            else {
                Toast.makeText(view.getContext(), R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
                Objects.requireNonNull(getActivity()).finish();
            }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private static class MapFragmentLocationCallback
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<MapFragment> activityWeakReference;

        MapFragmentLocationCallback(MapFragment activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.
         */

        @Override
        public void onSuccess(LocationEngineResult result) {
            MapFragment fragment = activityWeakReference.get();

            if (fragment != null) {
                Location location = result.getLastLocation();
                if (location == null) return;

                // Pass the new location to the Maps SDK's LocationComponent
                if (fragment.mapboxMap != null && result.getLastLocation() != null) {
                    fragment.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
                }


            }
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location can not be captured
         *
         * @param exception the exception message
         */
        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.d("LocationChangeActivity", Objects.requireNonNull(exception.getLocalizedMessage()));
            MapFragment fragment = activityWeakReference.get();
            if (fragment != null)
                Toast.makeText(fragment.getContext(), exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show(); // will the context be good ?
        }
    }

}