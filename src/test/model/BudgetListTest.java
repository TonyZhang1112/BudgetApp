package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BudgetListTest {
    public BudgetList budgetList;
    public Budget testBudget = new Budget(1, "Hi There");

    @BeforeEach
    public void runBefore() {
        budgetList = new BudgetList(); // Has a budget by default
    }

    @Test
    public void addBudgetTest () {
        budgetList.addBudget(testBudget);
        assertEquals(budgetList.getSize(), 2);
        assertEquals(budgetList.giveBudget(1), testBudget);
    }

    @Test
    public void delBudgetTest () {
        assertEquals(budgetList.getSize(), 1);
        budgetList.delBudget(0);
        assertEquals(budgetList.getSize(), 0);
    }
}
