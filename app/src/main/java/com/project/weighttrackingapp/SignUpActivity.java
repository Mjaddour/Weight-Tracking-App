package com.project.weighttrackingapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import database.DatabaseHelper;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        // Initialize UI components
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.passwordConfirm);

        Button signUpButton = findViewById(R.id.signupButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp() {
        // Validate email and password here

        // Check if the email is in a valid format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText().toString()).matches()) {
            Toast.makeText(SignUpActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        String enteredPassword = passwordEditText.getText().toString();

        if (enteredPassword.length() < 8) {
            Toast.makeText(SignUpActivity.this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the password contains at least one special character
        if (!enteredPassword.matches(".*[!@#$%^&*+=?-].*")) {  // Regex for at least one special character
            Toast.makeText(SignUpActivity.this, "Password must contain at least one special character (!@#$%^&*+=?-)", Toast.LENGTH_SHORT).show();
            return;
        }

        String enteredConfirmPassword = confirmPasswordEditText.getText().toString();

        // Check if the passwords match
        if (!enteredPassword.equals(enteredConfirmPassword)) {
            Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }


        // Open the database for writing
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Check if the user with the entered email already exists in the database
        if (!isUserExists(db, emailEditText.getText().toString())) {
            // User does not exist, insert the new user into the database
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_USERNAME, emailEditText.getText().toString());
            values.put(DatabaseHelper.COLUMN_PASSWORD, confirmPasswordEditText.getText().toString());

            // Insert the new user into the 'users' table
            long newRowId = db.insert(DatabaseHelper.TABLE_USERS, null, values);

            if (newRowId != -1) {
                // User registration successful
                Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                // Navigate to the DataActivity or any other activity
                Intent intent = new Intent(SignUpActivity.this, DataActivity.class);
                startActivity(intent);
                finish(); // Optional: Finish the SignUpActivity so the user can't go back
            } else {
                // User registration failed
                Toast.makeText(SignUpActivity.this, "Registration failed. Try again.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // User with the entered email already exists
            Toast.makeText(SignUpActivity.this, "User with this email already exists", Toast.LENGTH_SHORT).show();
        }

        // Close the database connection
        db.close();
    }

    private boolean isUserExists(SQLiteDatabase db, String email) {
        // Check if the user with the entered email already exists in the 'users' table
        String[] projection = {DatabaseHelper.COLUMN_ID};
        String selection = DatabaseHelper.COLUMN_USERNAME + "=?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean userExists = cursor.getCount() > 0;

        // Close the cursor
        cursor.close();

        return userExists;
    }
}
