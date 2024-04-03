package com.project.weighttrackingapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import database.DatabaseHelper_DataActivity;
import database.FoodEntry;

public class DataActivity extends AppCompatActivity {

    // Constants for SharedPreferences
    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_IS_USER_LOGGED_IN = "IsUserLoggedIn";

    private EditText foodEditText;
    private EditText quantityEditText;
    private EditText weightEditText;
    private Button addButton;
    private GridView gridView;
    private Button deleteButton;
    private ArrayAdapter<String> adapter;
    private List<String> foodEntries;
    private DatabaseHelper_DataActivity databaseHelperData;
    private Button accessRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the user is logged in
        if (!isUserLoggedIn()) {
            // If not logged in, redirect to the login activity
            Intent loginIntent = new Intent(DataActivity.this, MainActivity.class);
            startActivity(loginIntent);
            finish(); // Close the current activity to prevent going back
            return;
        }

        // Set the layout for the activity
        setContentView(R.layout.data_baseinfo);

        // Initialize UI components
        foodEditText = findViewById(R.id.foodEditText);
        quantityEditText = findViewById(R.id.quantityEditText);
        weightEditText = findViewById(R.id.weight);
        addButton = findViewById(R.id.addButton);
        gridView = findViewById(R.id.gridView);
        accessRequest = findViewById(R.id.accessRequest);
        deleteButton = findViewById(R.id.deleteButton);

        // Set the choice mode for the GridView
        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);

        // Initialize data structures
        foodEntries = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, foodEntries);
        gridView.setAdapter(adapter);

        // Initialize the database helper
        databaseHelperData = new DatabaseHelper_DataActivity(this);

        // Set click listeners for buttons
        accessRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAccessRequest();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFoodEntry();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedFoodEntries();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                deleteFoodEntry(position);
            }
        });
    }

    // Method to check if the user is logged in
    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_USER_LOGGED_IN, false);
    }

    // Method to handle access request
    public void setAccessRequest() {
        Intent intent = new Intent(DataActivity.this, NotificationActivity.class);
        startActivity(intent);
    }

    // Method to update the GridView with data from the database
    private void updateGridView() {
        foodEntries.clear();
        List<FoodEntry> entries = databaseHelperData.getAllFoodEntries();
        for (FoodEntry entry : entries) {
            foodEntries.add(entry.toString());
        }
        adapter.notifyDataSetChanged();
    }

    // Method to delete selected food entries
    private void deleteSelectedFoodEntries() {
        SparseBooleanArray checkedItems = gridView.getCheckedItemPositions();

        for (int i = 0; i < checkedItems.size(); i++) {
            int position = checkedItems.keyAt(i);
            if (checkedItems.valueAt(i)) {
                deleteFoodEntry(position);
            }
        }

        // Clear the selection after deletion
        gridView.clearChoices();
    }

    // Method to delete a specific food entry
    private void deleteFoodEntry(int position) {
        List<FoodEntry> entries = databaseHelperData.getAllFoodEntries();
        if (position >= 0 && position < entries.size()) {
            FoodEntry entryToDelete = entries.get(position);
            long result = databaseHelperData.deleteFoodEntry(entryToDelete);

            if (result > 0) {
                Toast.makeText(this, "Food entry deleted successfully", Toast.LENGTH_SHORT).show();
                updateGridView();
            } else {
                Toast.makeText(this, "Failed to delete food entry", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to save a new food entry
    private void saveFoodEntry() {
        String foodItem = foodEditText.getText().toString().trim();
        String quantity = quantityEditText.getText().toString().trim();
        String weightValue = weightEditText.getText().toString().trim();

        if (foodItem.isEmpty() || quantity.isEmpty() || weightValue.isEmpty()) {
            Toast.makeText(this, "Please enter all details.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add the food entry to the database
        long result = databaseHelperData.addFoodEntry(foodItem, quantity, weightValue);

        if (result > 0) {
            Toast.makeText(this, "Food entry saved successfully", Toast.LENGTH_SHORT).show();
            updateGridView();
        } else {
            Toast.makeText(this, "Failed to save food entry", Toast.LENGTH_SHORT).show();
        }

        // Clear the input fields
        foodEditText.setText("");
        quantityEditText.setText("");
        weightEditText.setText("");
    }
}
