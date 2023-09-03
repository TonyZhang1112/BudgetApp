package ui;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        try {
            new BudgetApp();
        } catch (FileNotFoundException e) {
            System.out.println("JSON file not found, BudgetApp cannot run.");
        }
    }
}
