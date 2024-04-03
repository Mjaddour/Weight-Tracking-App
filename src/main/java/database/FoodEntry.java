package database;

public class FoodEntry {
    // Fields to store information about a food entry
    private String foodName;
    private String quantity;
    private String weight;
    private String dateTime; // Added for date and time information

    // Constructor to initialize a FoodEntry object
    public FoodEntry(String foodName, String quantity, String weight, String dateTime) {
        this.foodName = foodName;
        this.quantity = quantity;
        this.weight = weight;
        this.dateTime = dateTime;
    }

    // Getter method to retrieve the food name
    public String getFoodName() {
        return foodName;
    }

    // Getter method to retrieve the quantity
    public String getQuantity() {
        return quantity;
    }

    // Getter method to retrieve the weight
    public String getWeight() {
        return weight;
    }

    // Getter method to retrieve the date and time
    public String getDateTime() {
        return dateTime;
    }

    // Override toString method to provide a string representation of the FoodEntry object
    @Override
    public String toString() {
        return "Food Entered:" +
                "Item = " + foodName + "\n" +
                "Quantity = " + quantity + "\n" +
                "Current Weight = " + weight + "\n" +
                "Date and Time = " + dateTime + "\n";
    }
}
