package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper_DataActivity extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "food_entries_db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "food_entries";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FOOD_NAME = "food_name";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_DATE_TIME = "date_time"; // Added column for date and time

    // Create table query
    private static final String CREATE_TABLE_FOOD_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_FOOD_NAME + " TEXT," +
                    COLUMN_QUANTITY + " TEXT," +
                    COLUMN_WEIGHT + " TEXT," +
                    COLUMN_DATE_TIME + " TEXT" +
                    ")";

    public DatabaseHelper_DataActivity(Context context) {
        // Constructor to initialize the database
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Execute the SQL statement to create the food_entries table when the database is created
        db.execSQL(CREATE_TABLE_FOOD_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Add any necessary logic for upgrading the database (e.g., altering tables)
        // For now, you can just drop and recreate the table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Method to add a new food entry to the database
    public long addFoodEntry(String foodName, String quantity, String weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FOOD_NAME, foodName);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_WEIGHT, weight);

        // Get the current date and time
        String dateTime = getCurrentDateTime();
        values.put(COLUMN_DATE_TIME, dateTime);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_NAME, null, values);

        if (newRowId == -1) {
            Log.e("Database", "Error inserting data into the database");
        }

        // Close the database connection
        db.close();

        return newRowId;
    }

    // Helper method to get the current date and time
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        return sdf.format(new Date());
    }

    // Method to retrieve all food entries from the database
    public List<FoodEntry> getAllFoodEntries() {
        List<FoodEntry> foodEntries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_ID,
                COLUMN_FOOD_NAME,
                COLUMN_QUANTITY,
                COLUMN_WEIGHT,
                COLUMN_DATE_TIME
        };

        Cursor cursor = db.query(
                TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String foodName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FOOD_NAME));
            String quantity = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
            String weight = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT));
            String dateTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE_TIME));

            FoodEntry foodEntry = new FoodEntry(id, foodName, quantity, weight, dateTime);
            foodEntries.add(foodEntry);
        }

        cursor.close();
        db.close();

        return foodEntries;
    }

    // Method to delete a food entry from the database
    public long deleteFoodEntry(FoodEntry foodEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result;

        try {
            // Define the selection criteria
            String selection = COLUMN_ID + " = ?";
            String[] selectionArgs = {String.valueOf(foodEntry.getId())};

            // Perform the delete operation
            result = db.delete(TABLE_NAME, selection, selectionArgs);
        } finally {
            // Close the database connection
            db.close();
        }

        return result;
    }

    // Method to update a food entry in the database
    public long updateFoodEntry(FoodEntry foodEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FOOD_NAME, foodEntry.getFoodName());
        values.put(COLUMN_QUANTITY, foodEntry.getQuantity());
        values.put(COLUMN_WEIGHT, foodEntry.getWeight());

        // Define the selection criteria to update the correct row
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(foodEntry.getId())};

        // Perform the update operation
        long result = db.update(TABLE_NAME, values, selection, selectionArgs);

        // Close the database connection
        db.close();

        return result;
    }
}
