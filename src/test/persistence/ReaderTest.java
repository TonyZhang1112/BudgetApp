package persistence;

import model.Budget;
import model.BudgetList;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

//NOTE: Modelled around JsonSerializationDemo
//      LINK: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

public class ReaderTest extends JsonTest {

    @Test
    void testReaderNonExistentFile() {
        Reader reader = new Reader("./data/madeUp.json");
        try {
            BudgetList budgetList = reader.read();
            fail("Expected IOException");
        } catch (IOException e) {
            // all good!
        }
    }

    @Test
    void testReaderEmptyBudgetList() {
        Reader reader = new Reader("./data/testReaderEmptyBudgetList.json");
        try {
            BudgetList budgetList = reader.read();
            assertEquals(0, budgetList.getSize());
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

    @Test
    void testReaderNormalBudgetList() {
        List<String> incomeNames1 = new ArrayList<String>();
        incomeNames1.add("dfgdsg");
        List<String> incomeNames2 = new ArrayList<String>();
        incomeNames2.add("g43e534g");
        List<String> expenseNames = new ArrayList<String>();
        expenseNames.add("tg54");
        List<Double> incomeAmts1 = new ArrayList<Double>();
        incomeAmts1.add(234234.0);
        List<Double> incomeAmts2 = new ArrayList<Double>();
        incomeAmts2.add(123.23);
        List<Double> expenseAmts = new ArrayList<Double>();
        expenseAmts.add(3453.23);
        List<String> noExpenseNames = new ArrayList<String>();
        List<Double> noExpenseAmts = new ArrayList<Double>();
        try {
            Reader reader = new Reader("./data/testReaderNormalBudgetList.json");
            BudgetList budgetList = reader.read();
            List<Budget> budgets = budgetList.getBudgets();
            assertEquals(2, budgetList.getSize());
            checkBudget("New Budget", false, incomeNames1, expenseNames, incomeAmts1,
                    expenseAmts, budgets.get(0));
            checkBudget("g4tg", true, incomeNames2, noExpenseNames, incomeAmts2,
                    noExpenseAmts, budgets.get(1));
        } catch (IOException e) {
            fail("Couldn't read from file");
        }
    }

}
