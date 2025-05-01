package com.example.drawer.ui.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.drawer.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "googleMap";
    private static final long LOCATION_UPDATE_INTERVAL = 10000; // 10 seconds in milliseconds
    private static final float MIN_DISTANCE = 10.0f; // Minimum distance threshold in meters
    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Location lastLocation;

    // Declare routePoints and routePolyline
    private final List<LatLng> routePoints = new ArrayList<>();
    private Polyline routePolyline;

    // Markers for starting and last positions
    private Marker startMarker;
    private Marker lastMarker;

    private Handler locationUpdateHandler;
    private boolean markByTime = true; // Default to time-based marking

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = root.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Show the dialog to choose marking logic
        showMarkingChoiceDialog();

        return root;
    }

    private void showMarkingChoiceDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Choose Marking Logic")
                .setMessage("How would you like to mark your location?")
                .setPositiveButton("Mark by Distance", (dialog, which) -> {
                    markByTime = false; // Use distance-based logic
                    startLocationUpdates();
                })
                .setNegativeButton("Mark by Time", (dialog, which) -> {
                    markByTime = true; // Use time-based logic
                    startLocationUpdates();
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        // Check location permissions
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        // Enable location layer
        googleMap.setMyLocationEnabled(true);
    }

    private void startLocationUpdates() {
        locationUpdateHandler = new Handler(Looper.getMainLooper());

        if (markByTime) {
            // Time-based logic
            locationUpdateHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        return;
                    }

                    fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                        if (location != null) {
                            handleNewLocation(location);
                        }
                    });

                    // Schedule the next update
                    locationUpdateHandler.postDelayed(this, LOCATION_UPDATE_INTERVAL);
                }
            }, LOCATION_UPDATE_INTERVAL);
        } else {
            // Distance-based logic
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(LOCATION_UPDATE_INTERVAL);

            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        if (lastLocation == null || location.distanceTo(lastLocation) >= MIN_DISTANCE) {
                            handleNewLocation(location);
                        }
                    }
                }
            }, Looper.getMainLooper());
        }
    }

    private void handleNewLocation(Location location) {
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        // Check if this is the first point
        if (routePoints.isEmpty()) {
            // Add a red marker for the starting point
            startMarker = googleMap.addMarker(new MarkerOptions()
                    .position(currentLatLng)
                    .title("Starting Point")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

            // Add a green marker for the last position (initially the same as the starting point)
            lastMarker = googleMap.addMarker(new MarkerOptions()
                    .position(currentLatLng)
                    .title("Last Position")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        } else {
            // Add a smaller yellow dot with a black contour for subsequent points
            googleMap.addCircle(new CircleOptions()
                    .center(currentLatLng)
                    .radius(0.5) // Reduced radius for smaller breadcrumbs
                    .strokeColor(0xFF000000) // Black contour
                    .fillColor(0xFFFFFF00)); // Yellow fill

            // Update the last marker to the new position
            if (lastMarker != null) {
                lastMarker.remove(); // Remove the old last marker
            }
            lastMarker = googleMap.addMarker(new MarkerOptions()
                    .position(currentLatLng)
                    .title("Last Position")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }

        // Add the current location to the route points
        routePoints.add(currentLatLng);

        // Update the polyline connecting the route points
        if (routePolyline == null) {
            routePolyline = googleMap.addPolyline(new PolylineOptions()
                    .addAll(routePoints)
                    .color(0xFF0000FF) // Blue color
                    .width(5)); // Line width
        } else {
            routePolyline.setPoints(routePoints);
        }

        // Move the camera to the current location
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));

        Log.d(TAG, "Location: Lat=" + location.getLatitude() + ", Lng=" + location.getLongitude());

        // Update the last location
        lastLocation = location;

        // Fetch current date
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

        // Fetch device Android ID instead of IMEI
        String deviceId = getDeviceId();

        // Send the position to the backend
        savePositionToBackend(location.getLatitude(), location.getLongitude(), currentDate, deviceId);
    }

    private String getDeviceId() {
        return Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (locationUpdateHandler != null) {
            locationUpdateHandler.removeCallbacksAndMessages(null);
        }
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void savePositionToBackend(double latitude, double longitude, String date, String imei) {
        // Your backend logic here
    }
}
