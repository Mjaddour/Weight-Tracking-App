package database;

public class FoodEntry {
    private int id;            // Unique ID for each entry
    private String foodName;
    private String quantity;
    private String weight;
    private String dateTime;

    // Constructor for creating new entries without an ID
    public FoodEntry(String foodName, String quantity, String weight, String dateTime) {
        this.foodName = foodName;
        this.quantity = quantity;
        this.weight = weight;
        this.dateTime = dateTime;
    }

    // Constructor for entries with an ID (used for updates or querying the database)
    public FoodEntry(int id, String foodName, String quantity, String weight, String dateTime) {
        this.id = id;
        this.foodName = foodName;
        this.quantity = quantity;
        this.weight = weight;
        this.dateTime = dateTime;
    }

    // Getter for id
    public int getId() {
        return id;
    }

    // Setter for id
    public void setId(int id) {
        this.id = id;
    }

    // Getter for food name
    public String getFoodName() {
        return foodName;
    }

    // Setter for food name
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    // Getter for quantity
    public String getQuantity() {
        return quantity;
    }

    // Setter for quantity
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    // Getter for weight
    public String getWeight() {
        return weight;
    }

    // Setter for weight
    public void setWeight(String weight) {
        this.weight = weight;
    }

    // Getter for date and time
    public String getDateTime() {
        return dateTime;
    }

    // Setter for date and time
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "Date and Time: "+ dateTime + "\n" +
                "Food: " + foodName + "\n" +
                "Quantity: " + quantity + "\n" +
                "Weight: " + weight + " lbs";
    }
}
