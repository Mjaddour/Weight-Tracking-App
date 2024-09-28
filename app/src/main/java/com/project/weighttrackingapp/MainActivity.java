package com.project.weighttrackingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import database.AppConstants;
import database.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button loginButton;
    private Button signupButton;
    int counter = 3;
    private DatabaseHelper databaseHelper;
    private ImageButton eyeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        email = findViewById(R.id.email);
        password = findViewById(R.id.email_address);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Set click listeners for buttons
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    // Toggle password visibility
    private void togglePasswordVisibility() {
        int inputType = password.getInputType();
        if (isPasswordVisible(inputType)) {
            password.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            password.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        password.setSelection(password.getText().length());
    }

    // Check if the password is visible
    private boolean isPasswordVisible(int inputType) {
        return (inputType & InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) > 0;
    }

    // Handle login logic
    private void logIn() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String enteredEmail = email.getText().toString();
        String enteredPassword = password.getText().toString();

        // Query to check if the user exists in the database
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                null,
                databaseHelper.getUsernameColumnName() + " = ? AND " + databaseHelper.getPasswordColumnName() + " = ?",
                new String[]{enteredEmail, enteredPassword},
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            // User exists, successful login
            Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

            // Set the user as logged in
            setLoggedInStatus(true);

            // Start the DataActivity
            Intent intent = new Intent(MainActivity.this, DataActivity.class);
            startActivity(intent);
        } else {
            // User doesn't exist or incorrect credentials
            counter--;
            if (counter == 0) {
                loginButton.setEnabled(false);
                Toast.makeText(MainActivity.this, "Login failed. No attempts remaining.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Login failed. Attempts remaining: " + counter, Toast.LENGTH_SHORT).show();
            }

            // Set the user as not logged in
            setLoggedInStatus(false);
        }

        cursor.close();
        db.close();
    }

    // Set the user's login status in SharedPreferences
    private void setLoggedInStatus(boolean isLoggedIn) {
        SharedPreferences prefs = getSharedPreferences(AppConstants.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(AppConstants.KEY_IS_USER_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    // Handle sign-up logic
    private void signUp() {
        Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(intent);
    }
}
