package com.example.shopfinder2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;  // <-- Add this import
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";
    private FirebaseAuth mAuth; // FirebaseAuth instance
    private LocationReceiver locationReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize the receiver
        locationReceiver = new LocationReceiver();

        // Register the receiver for the "NEW_LOCATION_ADDED" broadcast
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(locationReceiver, new IntentFilter("com.example.shopfinder2.NEW_LOCATION_ADDED"));

        // Bind Buttons
        Button myBookingsButton = findViewById(R.id.myBookingsButton);
        Button studiosButton = findViewById(R.id.studiosButton);
        Button mapButton = findViewById(R.id.mapButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        // Check if user is logged in
        FirebaseUser user = mAuth.getCurrentUser();

        // If the user is not logged in, redirect to LoginActivity
        if (user == null) {
            Intent loginIntent = new Intent(DashboardActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish(); // Close DashboardActivity
            return; // Prevent further execution of code in onCreate
        }

        // Show/hide buttons based on login status
        myBookingsButton.setVisibility(user != null ? Button.VISIBLE : Button.INVISIBLE);
        studiosButton.setVisibility(Button.VISIBLE);
        mapButton.setVisibility(user != null ? Button.VISIBLE : Button.INVISIBLE);

        // Navigate to Profile Activity (only if logged in)
        myBookingsButton.setOnClickListener(v -> {
            if (user != null) {
                Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(DashboardActivity.this, "Please log in to update your profile", Toast.LENGTH_SHORT).show();
            }
        });

        // Navigate to About Us Activity
        studiosButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AboutUsActivity.class);
            startActivity(intent);
        });

        // Navigate to Map Activity (only if logged in)
        mapButton.setOnClickListener(v -> {
            if (user != null) {
                Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(DashboardActivity.this, "Please log in to find a studio", Toast.LENGTH_SHORT).show();
            }
        });

        // Log Out functionality
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut(); // Log out the user
            Toast.makeText(DashboardActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close the app for now
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the receiver when the activity is destroyed
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationReceiver);
    }
}
