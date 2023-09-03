package model;


import com.sun.org.apache.xpath.internal.operations.Neg;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BudgetTest {
    public Budget budget1; //Monthly test
    public Budget budget2; //Yearly test

    @BeforeEach
    public void runBefore() {
        budget1 = new Budget(0, "BudgetTest1");
        budget2 = new Budget(1, "BudgetTest2");
    }

    @Test
    public void addItemTest() {
        try {
            budget1.addItem(100000, "Job", 0);
            budget1.addItem(100000, "Job", 0);
            budget1.addItem(4563463, "Job", 0);
            budget1.addItem(14, "Job", 1);
            budget1.addItem(100, "Allowance", 0);
            budget1.addItem(1234, "", 0);
        } catch (NegativeAmountException e) {
            fail("Unexpected NegativeAmountException");
        }
        try {
            budget1.addItem(-14, "Jobsdf", 1);
            fail("Expected NegativeAmountException");
        } catch (NegativeAmountException e) {
            //all good!
        }
        assertEquals(budget1.getIncome().get(0).getAmount(), 100000, 0.001);
        assertEquals(budget1.getIncome().get(0).getTitle(), "Job");
        assertEquals(budget1.getIncome().get(1).getAmount(), 100, 0.001);
        assertEquals(budget1.getIncome().get(1).getTitle(), "Allowance");
        assertEquals(budget1.getExpenses().get(0).getAmount(), 14, 0.001);
        assertEquals(budget1.getExpenses().get(0).getTitle(), "Job");
        assertEquals(budget1.getTotalExpenses(), 14, 0.001);
        assertEquals(budget1.getTotalIncome(), 101334.0, 0.001);
        assertEquals(budget1.getNetAmount(), 101320.0, 0.001);
        assertEquals(budget1.getIncome().get(2).getTitle(), "New Item");
    }

    @Test
    public void changeTimePeriodTest() {
        try {
            budget1.addItem(5, "Allowance", 0);
        } catch (NegativeAmountException e) {
            fail("Unexpected NegativeAmountException");
        }
        budget1.changeTimePeriod();
        assertTrue(budget1.getFrequency());
        assertEquals(budget1.getTotalIncome(), 5, 0.001);
        assertEquals(budget1.getIncome().get(0).getAmount(), 5, 0.001);
        budget1.changeTimePeriod();
        assertFalse(budget1.getFrequency());
        assertEquals(budget1.getTotalIncome(), 5, 0.001);
        assertEquals(budget1.getIncome().get(0).getAmount(), 5, 0.001);
    }

    @Test
    public void changeTimePeriodAndAmounts() {
        try {
            budget2.addItem(1206, "Job", 0);
            budget2.addItem(120000, "Robbing Banks (Different Job)", 0);
            budget2.addItem(366, "Food", 1);
        } catch (NegativeAmountException e) {
            fail("Unexpected NegativeAmountException");
        }
        assertEquals(budget2.getTotalIncome(), 121206, 0.001);
        assertEquals(budget2.getTotalExpenses(), 366, 0.001);
        budget2.changeTimePeriodAndAmounts();
        assertFalse(budget2.getFrequency());
        assertEquals(budget2.getTotalIncome(), 10100.5, 0.001);
        assertEquals(budget2.getTotalExpenses(), 30.5, 0.001);
        assertEquals(budget2.getNetAmount(), 10100.5-30.5, 0.001);
        assertEquals(budget2.getIncome().get(0).getAmount(), 100.5, 0.001);
        assertEquals(budget2.getIncome().get(1).getAmount(), 10000, 0.001);
        assertEquals(budget2.getExpenses().get(0).getAmount(), 30.5, 0.001);
        budget2.changeTimePeriodAndAmounts();
        assertTrue(budget2.getFrequency());
        assertEquals(budget2.getTotalIncome(), 121206, 0.001);
        assertEquals(budget2.getTotalExpenses(), 366, 0.001);
        assertEquals(budget2.getNetAmount(), 121206-366, 0.001);
        assertEquals(budget2.getIncome().get(0).getAmount(), 1206, 0.001);
        assertEquals(budget2.getIncome().get(1).getAmount(), 120000, 0.001);
        assertEquals(budget2.getExpenses().get(0).getAmount(), 366, 0.001);
    }

    @Test
    public void deleteIncomeTest() {
        try {
            budget1.addItem(5, "Allowance", 0);
            budget1.addItem(500000, "Heists", 0);
        } catch (NegativeAmountException e) {
            fail("Unexpected NegativeAmountException");
        }
        assertTrue(budget1.deleteIncome(0));
        assertFalse(budget1.deleteIncome(1));
        assertEquals(budget1.getTotalIncome(), 500000, 0.001);
        assertEquals(budget1.getIncome().get(0).getAmount(), 500000, 0.001);
        assertEquals(budget1.getIncome().get(0).getTitle(), "Heists");
    }

    @Test
    public void deleteExpenseTest() {
        try {
            budget1.addItem(5000, "Allowance", 0);
            budget1.addItem(500, "Ransom Money", 1);
            budget1.addItem(1500, "More Ransom Money", 1);
        } catch (NegativeAmountException e) {
            fail("Unexpected NegativeAmountException");
        }
        budget1.deleteExpense(1);
        assertFalse(budget1.deleteExpense(1));
        assertEquals(budget1.getTotalExpenses(), 500, 0.001);
        assertEquals(budget1.getNetAmount(), 4500, 0.001);
        assertEquals(budget1.getIncome().get(0).getAmount(), 5000, 0.001);
        assertEquals(budget1.getIncome().get(0).getTitle(), "Allowance");
        assertEquals(budget1.getExpenses().get(0).getAmount(), 500, 0.001);
        assertEquals(budget1.getExpenses().get(0).getTitle(), "Ransom Money");
    }

    @Test
    public void changeIncomeNameTest() {
        try {
            budget1.addItem(5000, "Stealing Lunch Money", 0);
            budget1.addItem(500000, "Heists", 0);
        } catch (NegativeAmountException e) {
            fail("Unexpected NegativeAmountException");
        }
        budget1.changeIncomeName(0, "Heists");
        assertEquals(budget1.getIncome().get(0).getTitle(), "Stealing Lunch Money");
        budget1.changeIncomeName(0, "Morally acquiring money");
        assertEquals(budget1.getIncome().get(0).getTitle(), "Morally acquiring money");
    }

    @Test
    public void changeExpenseNameTest() {
        try {
            budget1.addItem(5000, "Stealing Lunch Money", 0);
            budget1.addItem(500000, "Heists", 0);
            budget1.addItem(500, "Ransom Money", 1);
            budget1.addItem(1500, "More Ransom Money", 1);
        } catch (NegativeAmountException e) {
            fail("Unexpected NegativeAmountException");
        }
        budget1.changeExpenseName(0, "More Ransom Money");
        assertEquals(budget1.getExpenses().get(0).getTitle(), "Ransom Money");
        budget1.changeExpenseName(0, "Food");
        assertEquals(budget1.getExpenses().get(0).getTitle(), "Food");
        assertEquals(budget1.getIncome().get(0).getTitle(), "Stealing Lunch Money");
    }

    @Test
    public void changeIncomeAmtTest() {
        try {
            budget1.addItem(5000, "Stealing Lunch Money", 0);
            budget1.addItem(500, "Ransom Money", 1);
        } catch (NegativeAmountException e) {
            fail("Unexpected NegativeAmountException");
        }
        budget1.changeIncomeAmt(0, 450.5);
        assertFalse(budget1.changeIncomeAmt(0, -450));
        assertEquals(budget1.getIncome().get(0).getAmount(), 450.5, 0.001);
        assertEquals(budget1.getExpenses().get(0).getAmount(), 500, 0.001);
        assertEquals(budget1.getTotalIncome(), 450.5, 0.001);
        assertEquals(budget1.getNetAmount(), -49.5, 0.001);
    }

    @Test
    public void changeExpenseAmtTest() {
        try {
            budget1.addItem(5000, "Stealing Lunch Money", 0);
            budget1.addItem(500, "Ransom Money", 1);
        } catch (NegativeAmountException e) {
            fail("Unexpected NegativeAmountException");
        }
        assertTrue(budget1.changeExpenseAmt(0, 4300));
        assertFalse(budget1.changeExpenseAmt(0, -450));
        assertEquals(budget1.getIncome().get(0).getAmount(), 5000, 0.001);
        assertEquals(budget1.getExpenses().get(0).getAmount(), 4300, 0.001);
        assertEquals(budget1.getTotalExpenses(), 4300, 0.001);
        assertEquals(budget1.getNetAmount(), 700, 0.001);
    }

    @Test
    public void changeNameTest() {
        assertEquals(budget1.getName(), "BudgetTest1");
        budget1.changeName("LE EPIC BUDGET");
        assertEquals(budget1.getName(), "LE EPIC BUDGET");
    }

    @Test
    public void addValidItemsNoException() {
        try {
            budget1.addItem(234.12, "Income", 0);
            budget1.addItem(100, "An Expense", 1);
        } catch (NegativeAmountException e) {
            fail("Unexpected NegativeAmountException");
        }
    }

    @Test
    public void addNegativeAmtToIncome() {
        try {
            budget1.addItem(-234.12, "Income", 0);
            budget1.addItem(100, "An Expense", 1);
            fail("Expected NegativeAmountException");
        } catch (NegativeAmountException e) {
            //all good!
        }
    }

    @Test
    public void addNegativeAmtToExpenses() {
        try {
            budget1.addItem(234.12, "Income", 0);
            budget1.addItem(-100, "An Expense", 1);
            fail("Expected NegativeAmountException");
        } catch (NegativeAmountException e) {
            //all good!
        }
    }
}