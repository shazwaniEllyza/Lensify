package com.example.shopfinder2;

import android.content.Context;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ChildEventListener;

public class LocationUpdateListener {

    private FirebaseDatabase database;
    private DatabaseReference locationsRef;
    private Context context;

    public LocationUpdateListener(Context context) {
        this.context = context;
        database = FirebaseDatabase.getInstance();
        locationsRef = database.getReference("locations");
    }

    public void startListening() {
        // Listen for child added event to get new locations
        locationsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                // Extract the values from the new studio
                String locationName = dataSnapshot.child("name").getValue(String.class);
                String lat = dataSnapshot.child("lat").getValue(String.class);
                String lng = dataSnapshot.child("lng").getValue(String.class);

                // Trigger the broadcast to notify the app about the new location
                sendLocationNotification(locationName, lat, lng);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                // Handle child changed event if needed
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // Handle child removed event if needed
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                // Handle child moved event if needed
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void sendLocationNotification(String locationName, String lat, String lng) {
        Intent intent = new Intent("com.example.shopfinder2.NEW_LOCATION_ADDED");
        intent.putExtra("locationName", locationName);
        intent.putExtra("lat", lat);
        intent.putExtra("lng", lng);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
