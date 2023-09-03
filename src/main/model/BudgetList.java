package model;

import org.json.JSONArray;
import org.json.JSONObject;
import persistence.Writable;

import java.util.ArrayList;
import java.util.List;

public class BudgetList implements Writable {
    private List<Budget> budgets;

    //EFFECTS: creates a new BudgetList with a new budget to start with
    public BudgetList() {
        budgets = new ArrayList<Budget>();
        budgets.add(new Budget(0, "Pre-made Budget"));
    }

    //REQUIRES: freq must be 0 or 1
    //MODIFIES: this
    //EFFECTS: adds new Budget to Budgets
    public void addBudget(Budget budget) {
        budgets.add(budget);
    }

    //REQUIRES: budgets.get(index) must exist
    //MODIFIES: this
    //EFFECTS: removes a budget from budgets
    public void delBudget(int index) {
        budgets.remove(index);
    }

    //REQUIRES: budgets.get(index) must exist
    //EFFECTS: returns a budget at the index
    public Budget giveBudget(int index) {
        return budgets.get(index);
    }

    //EFFECTS: return the number of budgets in BudgetList
    public int getSize() {
        return budgets.size();
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("budgets", budgetsToJson());
        return json;
    }

    // EFFECTS: returns budgets in this BudgetList as a JSON array
    private JSONArray budgetsToJson() {
        JSONArray jsonArray = new JSONArray();

        for (Budget b : budgets) {
            jsonArray.put(b.toJson());
        }

        return jsonArray;
    }

    //EFFECTS: returns list of Budgets
    public List<Budget> getBudgets() {
        return budgets;
    }

}
