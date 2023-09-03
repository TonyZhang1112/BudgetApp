package ui;

import java.io.FileNotFoundException;

//NOTE: ALL PERSISTENCE METHODS AND TESTS MODELLED AROUND JsonSerializationDemo
//      Link: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

public class Main {
    public static void main(String[] args) {
        try {
            new BudgetApp();
        } catch (FileNotFoundException e) {
            System.out.println("JSON file not found, BudgetApp cannot run.");
        }
    }
}
