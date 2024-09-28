package com.project.weighttrackingapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class NotificationActivity extends AppCompatActivity {
    private static final int SMS_PERMISSION_REQUEST_CODE = 1001;

    private Button requestPermissionButton;
    private TextView permissionStatusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification); // Set the correct layout file

        // Initialize UI components for SMS permission
        requestPermissionButton = findViewById(R.id.requestPermissionButton);
        permissionStatusTextView = findViewById(R.id.permissionStatusTextView);

        // Set click listener for the permission button
        requestPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSMSPermission();
            }
        });

        // Find the ImageView by its ID for navigation
        ImageView foodImageView = findViewById(R.id.foodImageView);

        // Set a click listener to navigate back to DataActivity
        foodImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to go back to DataActivity
                Intent intent = new Intent(NotificationActivity.this, DataActivity.class);
                startActivity(intent);
                // Optionally, if you want to close the current activity
                finish();
            }
        });
    }

    // Request SMS permission
    private void requestSMSPermission() {
        // Check if permission is not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    SMS_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted
            updatePermissionStatus("SMS permission is already granted.");
            Toast.makeText(this, "SMS permission is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            // Check if the permission request is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                updatePermissionStatus("SMS permission granted.");
            } else {
                // Permission denied
                updatePermissionStatus("SMS permission denied.");
            }
        }
    }

    // Update permission status text
    private void updatePermissionStatus(String status) {
        permissionStatusTextView.setText(status);
    }
}
