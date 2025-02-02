package com.example.shopfinder2;

import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.widget.Toast;

public class LocationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get data from the intent
        String locationName = intent.getStringExtra("locationName");
        String lat = intent.getStringExtra("lat");
        String lng = intent.getStringExtra("lng");

        // Show a Toast for testing purposes
        Toast.makeText(context, "New Location: " + locationName, Toast.LENGTH_SHORT).show();

        // Create a notification to show the new location update
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel for Android 8.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("location_updates", "Location Updates", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Create the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "location_updates")
                .setSmallIcon(R.drawable.ic_launcher_foreground)  // Your app's icon
                .setContentTitle("New Location Added!")
                .setContentText("Location: " + locationName + " (Lat: " + lat + ", Lng: " + lng + ")")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Send the notification
        notificationManager.notify(0, notificationBuilder.build());
    }
}
