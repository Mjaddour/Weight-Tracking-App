package com.project.weighttrackingapp;

import android.content.DialogInterface;
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

import androidx.appcompat.app.AlertDialog;
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

        // Update GridView on item click to show options for updating or deleting the entry
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showEditDeleteDialog(position);  // Call the method to show dialog on item click
            }
        });

        updateGridView();
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

    // Method to show a dialog with options to edit or delete an entry
    private void showEditDeleteDialog(final int position) {
        // Get the selected entry
        List<FoodEntry> entries = databaseHelperData.getAllFoodEntries();
        final FoodEntry selectedEntry = entries.get(position);

        // Create a dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(DataActivity.this);
        builder.setTitle("Choose an action");

        // Set up the Edit Text views inside the dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_update_entry, null);
        final EditText foodEditText = dialogView.findViewById(R.id.dialogFoodEditText);
        final EditText quantityEditText = dialogView.findViewById(R.id.dialogQuantityEditText);
        final EditText weightEditText = dialogView.findViewById(R.id.dialogWeightEditText);

        // Pre-fill the Edit Texts with the current entry's data
        foodEditText.setText(selectedEntry.getFoodName());
        quantityEditText.setText(selectedEntry.getQuantity());
        weightEditText.setText(selectedEntry.getWeight());

        // Set the dialog view
        builder.setView(dialogView);

        // Set the buttons for Update and Delete
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get the updated values
                String updatedFood = foodEditText.getText().toString().trim();
                String updatedQuantity = quantityEditText.getText().toString().trim();
                String updatedWeight = weightEditText.getText().toString().trim();

                // Update the selected entry in the database
                selectedEntry.setFoodName(updatedFood);
                selectedEntry.setQuantity(updatedQuantity);
                selectedEntry.setWeight(updatedWeight);

                long result = databaseHelperData.updateFoodEntry(selectedEntry);

                if (result > 0) {
                    Toast.makeText(DataActivity.this, "Entry updated successfully", Toast.LENGTH_SHORT).show();
                    updateGridView();
                } else {
                    Toast.makeText(DataActivity.this, "Failed to update entry", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete the selected entry
                long result = databaseHelperData.deleteFoodEntry(selectedEntry);

                if (result > 0) {
                    Toast.makeText(DataActivity.this, "Entry deleted successfully", Toast.LENGTH_SHORT).show();
                    updateGridView();
                } else {
                    Toast.makeText(DataActivity.this, "Failed to delete entry", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNeutralButton("Cancel", null);

        // Show the dialog
        builder.create().show();
    }
}
