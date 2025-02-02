package com.example.shopfinder2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Button btnSearch;
    private EditText searchDestination;
    private DatabaseReference mDatabase;
    private LatLng currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference("locations");

        // Initialize Places API with your API key
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyDpL5v-UdJ7JDw9D1rgy6nxweFiUhLexPY");  // Replace with your API key
        }

        // Initialize Location Services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // UI Elements
        btnSearch = findViewById(R.id.btn_search);
        searchDestination = findViewById(R.id.search_destination);

        // Map Fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Handle Search Button
        btnSearch.setOnClickListener(v -> {
            String location = searchDestination.getText().toString();
            if (!location.isEmpty()) {
                searchForLocation(location);
            } else {
                Toast.makeText(this, "Enter a destination to search", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Default location to zoom in (Sydney as example)
        LatLng defaultLocation = new LatLng(-34.0, 151.0);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10));

        // Check if the app has permission to access location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        // Enable the location layer (optional)
        mMap.setMyLocationEnabled(true);

        // Get current location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    }
                });

        // Retrieve locations from Firebase and add markers
        mDatabase.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    Double lat = snapshot.child("lat").getValue(Double.class);
                    Double lng = snapshot.child("lng").getValue(Double.class);

                    // If lat and lng are valid, create a LatLng object and add a marker
                    if (lat != null && lng != null) {
                        LatLng location = new LatLng(lat, lng);
                        // Add the marker with a title (name of the photographer or studio)
                        mMap.addMarker(new MarkerOptions().position(location).title(name));
                    }
                }
            } else {
                Log.e("Firebase", "Error getting data", task.getException());
            }
        });

        // Add Marker Click Listener to navigate to the marker's location
        mMap.setOnMarkerClickListener(marker -> {
            // Get the position of the marker clicked
            LatLng destination = marker.getPosition();

            // Open navigation with Directions
            openNavigation(currentLocation, destination);
            return true; // Indicate that the click event was handled
        });
    }

    // Method to open navigation in Google Maps
    private void openNavigation(LatLng origin, LatLng destination) {
        String uri = "https://www.google.com/maps/dir/?api=1&origin=" + origin.latitude + "," + origin.longitude
                + "&destination=" + destination.latitude + "," + destination.longitude
                + "&travelmode=driving"; // You can change the mode (driving, walking, etc.)

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps"); // This will open Google Maps
        startActivity(intent);
    }

    // Method to search for a location by name
    private void searchForLocation(String location) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocationName(location, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                Log.d("LocationSearch", "Found location: " + location + " at " + latLng);
                mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                Toast.makeText(this, "Location found: " + location, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e("GeocodingError", "Error: " + e.getMessage(), e);
            Toast.makeText(this, "Error searching for location: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, enable the location layer
                if (mMap != null) {
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
