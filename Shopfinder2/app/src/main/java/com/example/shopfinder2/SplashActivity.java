package com.example.shopfinder2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Assuming you want to wait for a few seconds before navigating
        new Handler().postDelayed(() -> {
            // Check if the user is logged in
            if (isUserLoggedIn()) {
                startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();  // Close SplashActivity after navigating
        }, 3000);  // Wait for 3 seconds (or adjust as needed)
    }

    private boolean isUserLoggedIn() {
        // Check if user is logged in using Firebase
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }
}
