package persistence;

import model.Budget;
import model.BudgetList;

//NOTE: Modelled around JsonSerializationDemo
//      LINK: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import model.NegativeAmountException;
import org.json.*;

public class Reader {
    private String src;

    // EFFECTS: constructs reader to read from source file
    public Reader(String source) {
        this.src = source;
    }

    // EFFECTS: reads budgets from file and returns it;
    // throws IOException if an error occurs reading data from file
    public BudgetList read() throws IOException {
        String jsonData = readFile(src);
        JSONObject jsonObject = new JSONObject(jsonData);
        return parseBudgetList(jsonObject);
    }

    // EFFECTS: reads source file as string and returns it
    private String readFile(String source) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(source), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));
        }

        return contentBuilder.toString();
    }

    // EFFECTS: parses BudgetList from JSON object and returns it
    private BudgetList parseBudgetList(JSONObject jsonObject) {
        BudgetList budgetList = new BudgetList();
        budgetList.delBudget(0);
        addBudgets(budgetList, jsonObject);
        return budgetList;
    }

    // MODIFIES: budgetList
    // EFFECTS: parses thingies from JSON object and adds them to workroom
    private void addBudgets(BudgetList budgetList, JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("budgets");
        for (Object json : jsonArray) {
            JSONObject nextBudget = (JSONObject) json;
            addBudget(budgetList, nextBudget);
        }
    }

    // MODIFIES: budgetList
    // EFFECTS: parses budget from JSON object and adds it to budgetList
    private void addBudget(BudgetList budgetList, JSONObject jsonObject) {
        boolean freq = jsonObject.getBoolean("freq");
        String name = jsonObject.getString("name");
        JSONArray jsonArray = jsonObject.getJSONArray("income");
        JSONArray jsonArray2 = jsonObject.getJSONArray("expenses");
        Budget budget = new Budget(freq ? 1 : 0, name);
        for (Object json : jsonArray) {
            JSONObject nextIncome = (JSONObject) json;
            addIncome(budget, nextIncome);
        }
        for (Object json : jsonArray2) {
            JSONObject nextExpense = (JSONObject) json;
            addExpense(budget, nextExpense);
        }

        budgetList.addBudget(budget);
    }

    // MODIFIES: budget
    // EFFECTS: parses income from JSON object and adds it to budget
    private void addIncome(Budget budget, JSONObject jsonObject) {
        double amt = jsonObject.getDouble("itemAmount");
        String name = jsonObject.getString("itemName");
        try {
            budget.addItem(amt, name, 0);
        } catch (NegativeAmountException e) {
            System.err.println("Unable to save.");
        }
    }

    // MODIFIES: budget
    // EFFECTS: parses expense from JSON object and adds it to budget
    private void addExpense(Budget budget, JSONObject jsonObject) {
        double amt = jsonObject.getDouble("itemAmount");
        String name = jsonObject.getString("itemName");
        try {
            budget.addItem(amt, name, 1);
        } catch (NegativeAmountException e) {
            System.err.println("Unable to save.");
        }
    }
}
