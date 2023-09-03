package persistence;

import model.Budget;
import model.BudgetList;
import model.NegativeAmountException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

//NOTE: Modelled around JsonSerializationDemo
//      LINK: https://github.students.cs.ubc.ca/CPSC210/JsonSerializationDemo

public class WriterTest extends JsonTest {

    @Test
    void testWriterInvalidFile() {
        try {
            BudgetList budgetList = new BudgetList();
            Writer writer = new Writer("./data/my\0illegal:fileName.json");
            writer.open();
            fail("IOException was expected");
        } catch (IOException e) {
            // all good!
        }
    }

    @Test
    void testWriterEmptyWorkroom() {
        try {
            BudgetList budgetList = new BudgetList();
            budgetList.delBudget(0);
            Writer writer = new Writer("./data/testWriterEmptyBudgetList.json");
            writer.open();
            writer.write(budgetList);
            writer.close();

            Reader reader = new Reader("./data/testWriterEmptyBudgetList.json");
            budgetList = reader.read();
            assertEquals(0, budgetList.getSize());
        } catch (IOException e) {
            fail("Exception should not have been thrown");
        }
    }

    @Test
    void testWriterNormalBudgetList() {
        List<String> incomeNames1 = new ArrayList<String>();
        incomeNames1.add("Income 1");
        List<String> incomeNames2 = new ArrayList<String>();
        incomeNames2.add("Income 2");
        List<String> expenseNames = new ArrayList<String>();
        expenseNames.add("An Expense");
        List<Double> incomeAmts1 = new ArrayList<Double>();
        incomeAmts1.add(234234.0);
        List<Double> incomeAmts2 = new ArrayList<Double>();
        incomeAmts2.add(123.23);
        List<Double> expenseAmts = new ArrayList<Double>();
        expenseAmts.add(3453.23);
        List<String> noExpenseNames = new ArrayList<String>();
        List<Double> noExpenseAmts = new ArrayList<Double>();
        try {
            BudgetList budgetList = new BudgetList();
            budgetList.delBudget(0);
            Budget budget1 = new Budget(0, "Budget 1");
            budget1.addItem(234234.0, "Income 1", 0);
            budget1.addItem(3453.23, "An Expense", 1);
            Budget budget2 = new Budget(1, "Budget 2");
            budget2.addItem(123.23, "Income 2", 0);
            budgetList.addBudget(budget1);
            budgetList.addBudget(budget2);
            Writer writer = new Writer("./data/testWriterNormalBudgetList.json");
            writer.open();
            writer.write(budgetList);
            writer.close();

            Reader reader = new Reader("./data/testWriterNormalBudgetList.json");
            budgetList = reader.read();
            List<Budget> budgets = budgetList.getBudgets();
            assertEquals(2, budgetList.getSize());
            checkBudget("Budget 1", false, incomeNames1, expenseNames, incomeAmts1,
                    expenseAmts, budgets.get(0));
            checkBudget("Budget 2", true, incomeNames2, noExpenseNames, incomeAmts2,
                    noExpenseAmts, budgets.get(1));
        } catch (IOException e) {
            fail("Couldn't read from file");
        } catch (NegativeAmountException e) {
            fail("Test inputs have negative amounts");
        }
    }
}
