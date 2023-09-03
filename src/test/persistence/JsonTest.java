package persistence;

import model.Budget;
import model.Item;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonTest {
    protected void checkBudget(String name, boolean freq,
                               List<String> incomeNames, List<String> expenseNames,
                               List<Double> incomeAmts, List<Double> expenseAmts,
                               Budget budget) {
        assertEquals(name, budget.getName());
        assertEquals(freq, budget.getFrequency());
        checkIncome(incomeNames, incomeAmts, budget.getIncome());
        checkExpenses(expenseNames, expenseAmts, budget.getExpenses());
    }

    private void checkIncome (List<String> names, List<Double> amounts, List<Item> income) {
        for (int index = 0; index < income.size(); index++) {
            assertEquals(names.get(index), income.get(index).getTitle());
            assertEquals(amounts.get(index), income.get(index).getAmount());
        }
    }

    private void checkExpenses (List<String> names, List<Double> amounts, List<Item> expenses) {
        for (int index = 0; index < expenses.size(); index++) {
            assertEquals(names.get(index), expenses.get(index).getTitle());
            assertEquals(amounts.get(index), expenses.get(index).getAmount());
        }
    }

}
