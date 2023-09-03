package model;

import org.json.JSONObject;
import persistence.Writable;

//Represents a single Income Source or Expense
public class Item implements Writable {
    private double amount;
    private String title;

    //REQUIRES: amnt >= 0, freq >= 0, name can't be of length 0, and addOrSub must be 0 or 1
    //EFFECTS: Creates a new Item with amount, frequency, a name, and whether it's Income or an Expense
    public Item(double amnt, String name) {
        amount = amnt;
        title = name;
    }

    //EFFECTS: returns amount in the Income or Expense
    public double getAmount() {
        return amount;
    }

    //EFFECTS: returns the Title of the Income or Expense
    public String getTitle() {
        return title;
    }

    //REQUIRES: newAmnt >= 0
    //MODIFIES: this
    //EFFECTS: changes the amount of the Income or Expense item
    public void changeAmount(double newAmnt) {
        amount = newAmnt;
    }

    //REQUIRES: newName must have a length greater than zero
    //MODIFIES: this
    //EFFECTS: Changes the name of an Item
    public void changeName(String newName) {
        title = newName;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("itemName", title);
        json.put("itemAmount", amount);
        return json;
    }
}
