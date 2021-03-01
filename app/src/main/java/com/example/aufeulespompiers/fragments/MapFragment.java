package com.example.aufeulespompiers.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.aufeulespompiers.R;
import com.example.aufeulespompiers.activities.AlertActivity;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
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
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.os.Looper.getMainLooper;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    // views
    private MapView mapView;
    private MapboxMap mapboxMap;

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


        Button takeAlert = view.findViewById(R.id.take_alert);
        Button alertPage = view.findViewById(R.id.alert_page);
        alertPage.setEnabled(true/*Replace by isAuth condition*/);
        takeAlert.setEnabled(true/*Replace by isAuth condition*/);

        alertPage.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), AlertActivity.class);
            intent.putExtra("statementId", "fWVp5tej0bD7DkKYUeOp"); // generalize
            startActivity(intent);
        });

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

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(new Style.Builder().fromUri(STYLE_URI)
                    , this::enableLocationComponent);

        mapboxMap.addMarker(new MarkerOptions()
                .position(new LatLng(48.85819, 2.29458))
                .setSnippet("lol\nghj")
                .title("Eiffel Tower"));

        mapboxMap.setOnMarkerClickListener(marker -> {
            marker.showInfoWindow(mapboxMap, mapView);
            // Show a toast with the title of the selected marker
            System.out.println("markerText = " + marker.getSnippet());
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