package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.List;

// Represents a list of Expenses and Income
public class Budget implements Writable {
    private double totalExpenses;
    private double totalIncome;
    private double netAmount;
    private List<Item> expenses;
    private List<Item> income;
    private boolean frequency; //false = Monthly, true = Yearly
    private String budgetName;


    //REQUIRES: freq must be 0 or 1
    //EFFECTS: Creates a new budget with no income or expenses, a name, and a Monthly or Yearly Time Scale
    public Budget(int freq, String name) { //freq = 0 means Monthly, 1 means Yearly
        totalExpenses = 0;
        totalIncome = 0;
        netAmount = 0;
        frequency = (freq == 1);
        expenses = new ArrayList<>();
        income = new ArrayList<>();
        budgetName = name;
    }

    //REQUIRES: amt >= 0, name.length() >= 0, type = 0 or 1, name cannot already exist in the item list
    //MODIFIES: this
    //EFFECTS: Adds new Item to Budget if the Title and Type doesn't already exist
    public boolean addItem(double amt, String name, int type) throws NegativeAmountException {
        Item item;
        if (amt <= 0.0) {
            throw new NegativeAmountException();
        }
        if (name.equals("")) {
            item = new Item(amt, "New Item");
        } else {
            item = new Item(amt, name);
        }
        if ((notInBudget(name, expenses) && (type == 1)) || (notInBudget(name, income) && (type == 0))) {
            if (type == 1) {
                expenses.add(item);
                totalExpenses += amt;
                netAmount -= amt;
            } else {
                income.add(item);
                totalIncome += amt;
                netAmount += amt;
            }
            return true;
        } else {
            return false;
        }
    }

    //helper function for addItem, newIncomeName and newExpenseName
    //EFFECTS: returns false if name exists in each item's names, true otherwise
    public boolean notInBudget(String name, List<Item> items) {
        for (Item i : items) {
            if (name.equals(i.getTitle())) {
                return false;
            }
        }
        return true;
    }

    //MODIFIES: this
    //EFFECTS: Changes time period of budget WITHOUT changing any income or expenses
    public void changeTimePeriod() {
        frequency = !frequency;
    }

    //MODIFIES: this
    //EFFECTS: Changes time period of budget AND changes ALL income and expenses
    public void changeTimePeriodAndAmounts() {
        if (frequency) {
            for (Item i: expenses) {
                i.changeAmount(i.getAmount() / 12.0);
            }
            for (Item i: income) {
                i.changeAmount(i.getAmount() / 12.0);
            }
            netAmount /= 12.0;
            totalExpenses /= 12.0;
            totalIncome /= 12.0;
        } else {
            for (Item i: expenses) {
                i.changeAmount(i.getAmount() * 12.0);
            }
            for (Item i: income) {
                i.changeAmount(i.getAmount() * 12.0);
            }
            netAmount *= 12.0;
            totalExpenses *= 12.0;
            totalIncome *= 12.0;
        }
        frequency = !frequency;
    }

    //REQUIRES: index must be less than length of list of income
    //MODIFIES: this
    //EFFECTS: deletes an Item from income at the index
    public boolean deleteIncome(int index) {
        if (index < income.size()) {
            Item toDelete = income.get(index);
            income.remove(index);
            totalIncome -= toDelete.getAmount();
            netAmount -= toDelete.getAmount();
            return true;
        } else {
            return false;
        }
    }

    //REQUIRES: index must be less than length of list of expense
    //MODIFIES: this
    //EFFECTS: deletes an Item from expenses at the index
    public boolean deleteExpense(int index) {
        if (index < expenses.size()) {
            Item toDelete = expenses.get(index);
            expenses.remove(index);
            totalExpenses -= toDelete.getAmount();
            netAmount += toDelete.getAmount();
            return true;
        } else {
            return false;
        }
    }

    //REQUIRES: newName must not already exist in income names, index < length of income list
    //MODIFIES: this
    //EFFECTS: changes the name of a specific item in the income list to newName
    public boolean changeIncomeName(int index, String newName) {
        if (notInBudget(newName, income)) {
            income.get(index).changeName(newName);
            return true;
        } else {
            return false;
        }
    }

    //REQUIRES: newName must not already exist in expense names, index < length of expense list
    //MODIFIES: this
    //EFFECTS: changes the name of a specific item in the expense list to newName
    public boolean changeExpenseName(int index, String newName) {
        if (notInBudget(newName, expenses)) {
            expenses.get(index).changeName(newName);
            return true;
        } else {
            return false;
        }
    }

    //REQUIRES: newAmt >= 0, index is less than length of income list
    //MODIFIES: this
    //EFFECTS: changes the amount of an income item
    public boolean changeIncomeAmt(int index, double newAmt) {
        if (newAmt >= 0) {
            double difference = newAmt - income.get(index).getAmount();
            income.get(index).changeAmount(newAmt);
            totalIncome += difference;
            netAmount += difference;
            return true;
        } else {
            return false;
        }
    }

    //REQUIRES: newAmt >= 0, index is less than length of expenses list
    //MODIFIES: this
    //EFFECTS: changes the amount of an expense item
    public boolean changeExpenseAmt(int index, double newAmt) {
        if (newAmt >= 0) {
            double difference = newAmt - expenses.get(index).getAmount();
            expenses.get(index).changeAmount(newAmt);
            totalExpenses += difference;
            netAmount -= difference;
            return true;
        } else {
            return false;
        }
    }


    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("name", budgetName);
        json.put("freq", frequency);
        json.put("income", incomeToJson());
        json.put("expenses", expensesToJson());
        return json;
    }

    // EFFECTS: returns income in this workroom as a JSON array
    private JSONArray incomeToJson() {
        JSONArray jsonArray = new JSONArray();

        for (Item i : income) {
            jsonArray.put(i.toJson());
        }

        return jsonArray;
    }

    // EFFECTS: returns income in this workroom as a JSON array
    private JSONArray expensesToJson() {
        JSONArray jsonArray = new JSONArray();

        for (Item i : expenses) {
            jsonArray.put(i.toJson());
        }

        return jsonArray;
    }

    //REQUIRES: newName.length() > 0
    //MODIFIES: this
    //EFFECTS: Changes the name of the Budget
    public void changeName(String newName) {
        budgetName = newName;
    }

    //EFFECTS: returns total expenses in the budget
    public double getTotalExpenses() {
        return totalExpenses;
    }

    //EFFECTS: returns total expenses in the budget
    public double getTotalIncome() {
        return totalIncome;
    }

    //EFFECTS: returns total expenses in the budget
    public List<Item> getExpenses() {
        return expenses;
    }

    //EFFECTS: returns total income in the budget
    public List<Item> getIncome() {
        return income;
    }

    //EFFECTS: returns total income minus total expenses
    public double getNetAmount() {
        return netAmount;
    }

    //EFFECTS: returns true if time period of Budget is yearly, false if monthly
    public boolean getFrequency() {
        return frequency;
    }

    //EFFECTS: returns name of budget
    public String getName() {
        return budgetName;
    }
}
