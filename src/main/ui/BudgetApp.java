package ui;

import model.Budget;
import model.BudgetList;
import model.Item;
import model.NegativeAmountException;
import persistence.Reader;
import persistence.Writer;

import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.File;
import javax.sound.sampled.*;

import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.Scanner;


import javax.swing.*;

import javax.swing.JFrame;


public class BudgetApp extends JFrame {
    public static final int WIDTH = 700;
    public static final int HEIGHT = 800;
    private static final String JSON_FILE = "./data/budgets.json";

    private Scanner input;
    private BudgetList budgetList;
    private Budget currentBudget;
    private Writer writer;
    private Reader reader;
    int counter;
    private JButton add;
    private JButton del;
    private JButton save;
    private JButton load;
    private JButton quit;
    private JTextField getInput;
    private JRadioButton monthly;
    private JRadioButton yearly;

    private boolean canSelect;
    private boolean deletingBudgets;
    private boolean deletingItems;
    private boolean changingIncome;
    private boolean changingExpense;
    private int currentItemIndex;

    private JPanel titlePanel;
    private JPanel optionPanel;
    private JPanel budgetPanel;
    private JPanel bottomPanel;
    private JPanel addPanel;
    private JPanel deletePanel;
    private JLabel alertMessage;
    private JLabel budgetTitle;
    private JPanel oneBgtPanel;
    private JPanel itemListPanel;
    private JPanel incomeListPanel;
    private JPanel expenseListPanel;

    private JPanel itemOptionPanel;
    private JPanel addIncPanel;
    private JPanel addExpPanel;
    private JPanel otherItemOptionsPanel;
    private JLabel addIncMessage;
    private JLabel addExpMessage;
    private JTextField getAddIncName;
    private JTextField getAddIncAmount;
    private JTextField getAddExpName;
    private JTextField getAddExpAmount;
    private JTextField getChangeName;
    private JTextField getChangeAmount;
    private JLabel itemAlertMessage;
    private JButton nameChange;
    private JButton itemDel;
    private JButton bgtQuit;
    private JButton changeNameSubmit;
    private JTextField newBgtName;
    private JPanel changeNamePanel;
    private JPanel delItemPanel;
    private JPanel changeItemPanel;
    private JLabel warningLabel;

    ImageIcon check;
    ImageIcon warning;
    AudioInputStream checkSound;
    AudioInputStream warningSound;
    Clip play;

    //EFFECTS: Runs BudgetApp
    public BudgetApp() throws FileNotFoundException {
        super("BudgetApp");
        canSelect = true;
        deletingBudgets = false;
        deletingItems = false;
        changingIncome = false;
        changingExpense = false;
        currentItemIndex = 0;
        check = new ImageIcon("./data/checkmark.png", "checkmark");
        warning = new ImageIcon("./data/warningicon.jpg", "warning");
        writer = new Writer(JSON_FILE);
        reader = new Reader(JSON_FILE);
        setUp();
        initializeGraphics();
        runBudgetApp();
    }

    //MODIFIES: this
    //EFFECTS: loads sounds
    private void addSounds()  {
        try {
            checkSound = AudioSystem.getAudioInputStream(new File("./data/checksound.wav"));
            warningSound = AudioSystem.getAudioInputStream(new File("./data/warningsound.wav"));
        } catch (UnsupportedAudioFileException e) {
            System.out.println("Audio file could not be supported.");
        } catch (IOException e) {
            System.out.println("Some IO exception has occurred.");
        }
    }

    //MODIFIES: this
    //EFFECTS: initializes all panels and sets them to visible
    private void initializeGraphics() {
        initOptions();
        initBottomPanel();
        initDeletePanel();
        initTitlePanel();
        initAddPanel();
        initBgtPanel();
        initBgtOptionsPanel();
        initNameChangePanel();
        initDelItemPanel();
        initChangeItemPanel();
        warningLabel = new JLabel();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(false);
        setVisible(true);
        setMinimumSize(new Dimension(WIDTH, HEIGHT));
        add(titlePanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);
        pack();
    }

    //MODIFIES: this
    //EFFECTS: initializes the title panel for the main menu
    private void initTitlePanel() {
        titlePanel = new JPanel();
        titlePanel.setLayout(new GridLayout(0, 1));
        titlePanel.getSize(new Dimension(0,0));
        drawBudgets();
    }

    //MODIFIES: this
    //EFFECTS: initializes the options panel for the main menu
    private void initOptions() {
        optionPanel = new JPanel();
        optionSetUp();
        add.addActionListener(this::actionSelected);
        save.addActionListener(this::actionSelected);
        load.addActionListener(this::actionSelected);
        quit.addActionListener(this::actionSelected);
        del.addActionListener(this::actionSelected);
    }

    //MODIFIES: this
    //EFFECTS: initializes the bottom panel for all individual option panels
    private void initBottomPanel() {
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(0, 1));
        bottomPanel.getSize(new Dimension(0,0));
        alertMessage = new JLabel("Select an option below, or a Budget to view it", SwingConstants.CENTER);
        bottomPanel.add(alertMessage);
        bottomPanel.add(optionPanel);
    }

    //MODIFIES: this
    //EFFECTS: sets up all options required for the main menu options
    private void optionSetUp() {
        add = new JButton("Add New Budget");
        add.setBackground(Color.GREEN);
        del = new JButton("Delete a Budget");
        del.setBackground(Color.MAGENTA);
        save = new JButton("Save Current Budgets");
        save.setBackground(Color.ORANGE);
        load = new JButton("Load Saved Budgets");
        load.setBackground(Color.CYAN);
        quit = new JButton("Exit BudgetApp");
        quit.setBackground(Color.LIGHT_GRAY);
        add.setActionCommand("a");
        del.setActionCommand("d");
        save.setActionCommand("s");
        load.setActionCommand("l");
        quit.setActionCommand("q");
        optionPanel.add(add);
        optionPanel.add(del);
        optionPanel.add(save);
        optionPanel.add(load);
        optionPanel.add(quit);
    }

    //MODIFIES: this
    //EFFECTS: draws budgets on main menu
    private void drawBudgets() {
        budgetPanel = new JPanel();
        JLabel title = new JLabel("BudgetApp", SwingConstants.CENTER);
        budgetPanel.add(title);
        budgetPanel.setLayout(new GridLayout(0, 1));
        budgetPanel.getSize(new Dimension(0,0));
        budgetPanel.setBackground(Color.ORANGE);
        int counter = 0;
        for (Budget bgt: budgetList.getBudgets()) {
            JButton budget = new JButton(bgt.getName());
            budget.setActionCommand(Integer.toString(counter));
            budget.addActionListener(this::actionSelected);
            if (bgt.getFrequency()) {
                budget.setBackground(Color.LIGHT_GRAY);
                budget.setText("Yearly Budget: " + bgt.getName());
            } else {
                budget.setBackground(Color.PINK);
                budget.setText("Monthly Budget: " + bgt.getName());
            }
            budgetPanel.add(budget);
            counter++;
        }
        titlePanel.add(budgetPanel, BorderLayout.NORTH);
    }

    //MODIFIES: this
    //EFFECTS: removes budgets from main menu
    private void unDrawBudgets() {
        titlePanel.remove(budgetPanel);
    }

    //MODIFIES: this
    //EFFECTS: removes options from main menu when an option is chosen
    private void deleteOptions() {
        bottomPanel.remove(optionPanel);
    }

    //MODIFIES: this
    //EFFECTS: creates interface for adding budgets
    private void initAddPanel() {
        addPanel = new JPanel();
        getInput = new JTextField(20);
        addPanel.add(getInput, SwingConstants.CENTER);
        monthly = new JRadioButton("Monthly");
        monthly.setSelected(true);
        yearly = new JRadioButton("Yearly");
        ButtonGroup monthOrYear = new ButtonGroup();
        monthOrYear.add(monthly);
        monthOrYear.add(yearly);
        addPanel.add(monthly);
        addPanel.add(yearly);
        JButton submitAdd = new JButton("Add!");
        submitAdd.setBackground(Color.GREEN);
        submitAdd.setActionCommand("as");
        submitAdd.addActionListener(this::actionSelected);
        addPanel.add(submitAdd);
    }

    //MODIFIES: this
    //EFFECTS: creates interface for deleting budgets
    private void initDeletePanel() {
        deletePanel = new JPanel();
        JButton cancel = new JButton("Cancel");
        cancel.setActionCommand("dc");
        cancel.setBackground(Color.GREEN);
        cancel.addActionListener(this::actionSelected);
        deletePanel.add(cancel);
    }

    //MODIFIES: this
    //EFFECTS: initializes budget panel for one budget
    private void initBgtPanel() {
        oneBgtPanel = new JPanel();
        oneBgtPanel.setLayout(new BorderLayout());
        budgetTitle = new JLabel("Budget", SwingConstants.CENTER);
        budgetTitle.setPreferredSize(new Dimension(WIDTH, 30));
        //oneBgtPanel.add(budgetTitle, BorderLayout.PAGE_START, SwingConstants.CENTER);
    }

    //MODIFIES: this
    //EFFECTS: pulls up interface for changing budget name
    private void initNameChangePanel() {
        changeNamePanel = new JPanel();
        newBgtName = new JTextField(20);
        changeNamePanel.add(newBgtName);
        changeNameSubmit = new JButton("Change");
        changeNameSubmit.setBackground(Color.CYAN);
        changeNameSubmit.addActionListener(this::itemAction);
        changeNameSubmit.setActionCommand("cs");
        changeNamePanel.add(changeNameSubmit);
    }

    //MODIFIES: this
    //EFFECTS: adds income and expenses of a budget to the GUI
    private void drawItems() {
        itemListPanel = new JPanel();
        itemListPanel.setLayout(new FlowLayout());
        itemListPanel.getSize(new Dimension(0,0));
        itemListPanel.setBackground(Color.LIGHT_GRAY);
        if (currentBudget.getFrequency()) {
            budgetTitle.setText("Yearly Budget \"" + currentBudget.getName() + "\"");
        } else {
            budgetTitle.setText("Monthly Budget \"" + currentBudget.getName() + "\"");
        }
        determineWarningOrNot(currentBudget.getNetAmount());
        oneBgtPanel.add(budgetTitle, BorderLayout.PAGE_START, SwingConstants.CENTER);
        initIncListPanel();
        initExpListPanel();
        counter = 0;
        drawIncome();
        drawExpenses();
        oneBgtPanel.add(itemListPanel, BorderLayout.CENTER);
    }

    //MODIFIES: this
    //EFFECTS: adds a warning or all good signal depending on if a budget has a surplus or deficit
    private void determineWarningOrNot(double netAmount) {
        oneBgtPanel.remove(warningLabel);
        if (netAmount < 0.0) {
            warningLabel = new JLabel("WARNING: BUDGET RUNS A DEFICIT", warning, SwingConstants.CENTER);
        } else {
            warningLabel = new JLabel("All good: Budget is even, or has a surplus!", check, SwingConstants.CENTER);
        }
        oneBgtPanel.add(warningLabel, BorderLayout.PAGE_END);

    }

    //MODIFIES: this
    //EFFECTS: initialized the income list
    private void initIncListPanel() {
        incomeListPanel = new JPanel();
        incomeListPanel.setLayout(new BoxLayout(incomeListPanel, BoxLayout.Y_AXIS));
        incomeListPanel.getSize(new Dimension(0,0));
        incomeListPanel.setPreferredSize(new Dimension(300, 450));
        incomeListPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    }

    //MODIFIES: this
    //EFFECTS: initializes the expenses list
    private void initExpListPanel() {
        expenseListPanel = new JPanel();
        expenseListPanel.setLayout(new BoxLayout(expenseListPanel, BoxLayout.Y_AXIS));
        expenseListPanel.getSize(new Dimension(0,0));
        expenseListPanel.setPreferredSize(new Dimension(300, 450));
        expenseListPanel.setBorder(BorderFactory.createLineBorder(Color.black));
    }


    //MODIFIES: this, counter
    //EFFECTS: draws a budget's income
    private void drawIncome() {
        incomeListPanel.removeAll();
        incomeListPanel.add(new JLabel("Income: $"
                        + Double.toString(Math.round(currentBudget.getTotalIncome() * 100) / 100.0)),
                SwingConstants.CENTER);
        for (Item income: currentBudget.getIncome()) {
            JButton inc = new JButton("$" + income.getAmount() + ": " + income.getTitle());
            inc.setActionCommand(Integer.toString(counter));
            inc.addActionListener(this::itemAction);
            incomeListPanel.add(inc);
            counter++;
        }
        itemListPanel.add(incomeListPanel);
        //oneBgtPanel.add(incomeListPanel, BorderLayout.LINE_START);
    }

    //MODIFIES: this, counter
    //EFFECTS: draws a budget's expenses
    private void drawExpenses() {
        expenseListPanel.removeAll();
        expenseListPanel.add(new JLabel("Expenses: $"
                + Double.toString(Math.round(currentBudget.getTotalExpenses() * 100) / 100.0)),
                SwingConstants.CENTER);
        for (Item expense: currentBudget.getExpenses()) {
            JButton exp = new JButton("$" + expense.getAmount() + ": " + expense.getTitle());
            exp.setActionCommand(Integer.toString(counter));
            exp.addActionListener(this::itemAction);
            expenseListPanel.add(exp);
            counter++;
        }
        itemListPanel.add(expenseListPanel);
        //oneBgtPanel.add(expenseListPanel, BorderLayout.LINE_END);
    }

    //MODIFIES: this
    //EFFECTS: initializes the panel of options for a single budget
    private void initBgtOptionsPanel() {
        itemOptionPanel = new JPanel();
        itemOptionPanel.setLayout(new GridLayout(0, 1));
        itemOptionPanel.getSize(new Dimension(0,0));
        addIncMessage = new JLabel("Add an Income:", SwingConstants.CENTER);
        itemOptionPanel.add(addIncMessage);
        initAddIncPanel();
        addExpMessage = new JLabel("Add an Expense:", SwingConstants.CENTER);
        itemOptionPanel.add(addExpMessage);
        initAddExpPanel();
        itemAlertMessage = new JLabel("Or choose an option below:", SwingConstants.CENTER);
        itemOptionPanel.add(itemAlertMessage);
        initAddOtherPanel();
        update(getGraphics());
    }

    //MODIFIES: this
    //EFFECTS: initializes option to add income to a budget
    private void initAddIncPanel() {
        addIncPanel = new JPanel();
        addIncPanel.add(new JLabel("Name: "));
        getAddIncName = new JTextField(20);
        addIncPanel.add(getAddIncName);
        addIncPanel.add(new JLabel("Amount: $"));
        getAddIncAmount = new JTextField(8);
        addIncPanel.add(getAddIncAmount);
        JButton submitIncAdd = new JButton("Add Income");
        submitIncAdd.setBackground(Color.GREEN);
        submitIncAdd.setActionCommand("ia");
        submitIncAdd.addActionListener(this::itemAction);
        addIncPanel.add(submitIncAdd);
        itemOptionPanel.add(addIncPanel);
    }

    //MODIFIES: this
    //EFFECTS: initializes option to add an expense to a budget
    private void initAddExpPanel() {
        addExpPanel = new JPanel();
        addExpPanel.add(new JLabel("Name: "));
        getAddExpName = new JTextField(20);
        addExpPanel.add(getAddExpName);
        addExpPanel.add(new JLabel("Amount: $"));
        getAddExpAmount = new JTextField(8);
        addExpPanel.add(getAddExpAmount);
        JButton submitIncAdd = new JButton("Add Expense");
        submitIncAdd.setBackground(Color.GREEN);
        submitIncAdd.setActionCommand("ea");
        submitIncAdd.addActionListener(this::itemAction);
        addExpPanel.add(submitIncAdd);
        itemOptionPanel.add(addExpPanel);
    }

    //MODIFIES: this
    //EFFECTS: adds other options to the item option panel
    private void initAddOtherPanel() {
        otherItemOptionsPanel = new JPanel();
        nameChange = new JButton("Change Budget Name");
        nameChange.setBackground(Color.CYAN);
        itemDel = new JButton("Delete an Item");
        itemDel.setBackground(Color.PINK);
        bgtQuit = new JButton("Return to Main Menu");
        bgtQuit.setBackground(Color.LIGHT_GRAY);
        nameChange.setActionCommand("c");
        itemDel.setActionCommand("d");
        bgtQuit.setActionCommand("q");
        nameChange.addActionListener(this::itemAction);
        itemDel.addActionListener(this::itemAction);
        bgtQuit.addActionListener(this::itemAction);
        otherItemOptionsPanel.add(nameChange);
        otherItemOptionsPanel.add(itemDel);
        otherItemOptionsPanel.add(bgtQuit);
        itemOptionPanel.add(otherItemOptionsPanel);
    }

    //MODIFIES: this
    //EFFECTS: initiates item delete panel
    private void initDelItemPanel() {
        delItemPanel = new JPanel();
        JButton cancel = new JButton("Cancel");
        cancel.setActionCommand("dc");
        cancel.setBackground(Color.GREEN);
        cancel.addActionListener(this::itemAction);
        delItemPanel.add(cancel);
    }

    //MODIFIES: this
    //EFFECTS: initiates item change panel
    private void initChangeItemPanel() {
        changeItemPanel = new JPanel();
        changeItemPanel.add(new JLabel("Name: "));
        getChangeName = new JTextField(20);
        changeItemPanel.add(getChangeName);
        changeItemPanel.add(new JLabel("Amount: $"));
        getChangeAmount = new JTextField(8);
        changeItemPanel.add(getChangeAmount);
        JButton changeItemSubmit = new JButton("Change!");
        changeItemSubmit.setBackground(Color.YELLOW);
        changeItemSubmit.setActionCommand("cis");
        changeItemSubmit.addActionListener(this::itemAction);
        changeItemPanel.add(changeItemSubmit);
    }

    //------------------------------------------------------------------------
    //                       START OF ACTION SELECTIONS
    //------------------------------------------------------------------------


    //MODIFIES: this
    //EFFECTS: executes command when button is pressed
    private void actionSelected(ActionEvent e) {
        if (e.getActionCommand().equals("s")) {
            executeSave();
        } else if (e.getActionCommand().equals("l")) {
            executeLoad();
        } else if (e.getActionCommand().equals("q")) {
            System.exit(0);
        } else if (e.getActionCommand().equals("a")) {
            executeAdd();
        } else if (e.getActionCommand().equals("as")) {
            verifyAddValid(getInput.getText(), yearly.isSelected());
        } else if (e.getActionCommand().equals("d")) {
            executeDelete();
        } else if (e.getActionCommand().equals("dc")) {
            returnToMain();
        } else {
            if (deletingBudgets) {
                completeBgtDelete(e.getActionCommand());
                returnToMain();
            } else if (canSelect) {
                setCurrentBudget(e.getActionCommand());
                transitionToBgt();
            }
        }
    }

    //EFFECTS: saves budgetList to file
    private void executeSave() {
        saveBudgetList();
        alertMessage.setText("Saved all budgets!");
        update(getGraphics());
    }

    //MODIFIES: this
    //EFFECTS: Loads budgets from JSON file
    private void executeLoad() {
        unDrawBudgets();
        loadBudgetList();
        drawBudgets();
        alertMessage.setText("Loaded all saved budgets!");
        update(getGraphics());
    }

    //MODIFIES: this
    //EFFECTS: pulls up interface to allow user to add new Budget to BudgetList
    private void executeAdd() {
        canSelect = false;
        deleteOptions();
        alertMessage.setText("Please enter the name of the new Budget, and select if it is monthly or yearly:");
        bottomPanel.add(addPanel);
        getInput.setText("");
        update(getGraphics());
    }

    //MODIFIES: this
    //EFFECTS: Verifies no duplicate or empty names, and if so, adds a new Budget
    private void verifyAddValid(String name, boolean selected) {
        int freq = getFreq(selected);
        Boolean valid = true;
        if (name.equals("")) {
            valid = false;
            alertMessage.setText("You must choose a name!");
        }
        for (Budget bgt: budgetList.getBudgets()) {
            if (name.equals(bgt.getName())) {
                valid = false;
                alertMessage.setText("You must choose a unique name!");
            }
        }
        if (valid) {
            budgetList.addBudget(new Budget(freq, name));
            bottomPanel.remove(addPanel);
            unDrawBudgets();
            drawBudgets();
            alertMessage.setText("Select an option below, or a Budget to view it");
            bottomPanel.add(optionPanel);
            canSelect = true;
        }
        update(getGraphics());
    }

    //EFFECTS: returns 1 if making a yearly budget, 0 otherwise
    private int getFreq(boolean selected) {
        if (selected) {
            return 1;
        } else {
            return 0;
        }
    }

    //MODIFIES: this
    //EFFECTS: pulls up interface to delete a budget
    private void executeDelete() {
        deletingBudgets = true;
        canSelect = false;
        bottomPanel.remove(optionPanel);
        alertMessage.setText("Select a Budget to delete.");
        bottomPanel.add(deletePanel);
        update(getGraphics());
    }

    //MODIFIES: this
    //EFFECTS: deletes a budget from the GUI
    private void completeBgtDelete(String toDelete) {
        int index = Integer.parseInt(toDelete);
        budgetList.delBudget(index);
        unDrawBudgets();
        drawBudgets();
        returnToMain();
    }

    //MODIFIES: this
    //EFFECTS: returns interface to main menu from delete menu
    private void returnToMain() {
        alertMessage.setText("Select an option below, or a Budget to view it");
        deletingBudgets = false;
        canSelect = true;
        bottomPanel.remove(deletePanel);
        bottomPanel.add(optionPanel);
        update(getGraphics());
    }

    //MODIFIES: this
    //EFFECTS: sets the active Budget to the one the user presses
    private void setCurrentBudget(String selection) {
        int index = Integer.parseInt(selection);
        currentBudget = budgetList.getBudgets().get(index);;
    }

    //MODIFIES: this
    private void transitionToBgt() {
        remove(titlePanel);
        remove(bottomPanel);
        drawItems();
        add(oneBgtPanel, BorderLayout.NORTH);
        add(itemOptionPanel, BorderLayout.SOUTH);
        getAddIncName.setText("");
        getAddExpName.setText("");
        getAddIncAmount.setText("");
        getAddExpAmount.setText("");
        playWarning(currentBudget.getNetAmount());
        setVisible(true);
        update(this.getGraphics());
    }


    //NOTE: Tips for implementing audio found on StackOverFlow forum post
    //      https://stackoverflow.com/questions/15526255/best-way-to-get-sound-on-button-press-for-a-java-calculator
    //MODIFIES: this
    //EFFECTS: plays a sound depending on if the budget is in surplus or deficit
    private void playWarning(double netAmount) {
        try {
            addSounds();
            play = AudioSystem.getClip();
            if (netAmount < 0.0) {
                play.open(warningSound);
            } else {
                play.open(checkSound);
            }
            play.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //----------------------------------------------------------------------------
    //                  START OF INDIVIDUAL BUDGET INTERFACE
    //----------------------------------------------------------------------------

    //MODIFIES: this
    //EFFECTS: executes command when button is pressed from within a Budget overview
    private void itemAction(ActionEvent e) {
        if (e.getActionCommand().equals("q")) {
            returnToMainFromBgt();
        } else if (e.getActionCommand().equals("ia")) {
            attemptAddToInc();
        } else if (e.getActionCommand().equals("ea")) {
            attemptAddToExp();
        } else if (e.getActionCommand().equals("c")) {
            executeNameChange();
        } else if (e.getActionCommand().equals("cs")) {
            changeBudgetName();
        } else if (e.getActionCommand().equals("d")) {
            executeItemDelete();
        } else if (e.getActionCommand().equals("dc")) {
            cancelDelete();
        } else if (e.getActionCommand().equals("cis")) {
            changeItem();
        } else {
            if (deletingItems) {
                killItem(Integer.parseInt(e.getActionCommand()));
            } else {
                executeChangeItem(Integer.parseInt(e.getActionCommand()));
            }
        }
    }

    //MODIFIES: this
    //EFFECTS: cancels item deletion
    private void cancelDelete() {
        itemOptionPanel.remove(delItemPanel);
        reInitItemOptions();
    }

    //MODIFIES: this
    //EFFECTS: changes name and/or amount of the current item
    private void changeItem() {
        if (changeItemName() && changeItemAmount()) {
            if (changingIncome) {
                if (!getChangeName.getText().equals("")) {
                    currentBudget.changeIncomeName(currentItemIndex, getChangeName.getText());
                }
                if (!getChangeAmount.getText().equals("")) {
                    currentBudget.changeIncomeAmt(currentItemIndex,
                            Math.round(Double.parseDouble(getChangeAmount.getText()) * 100) / 100.0);
                }
            } else {
                if (!getChangeName.getText().equals("")) {
                    currentBudget.changeExpenseName(currentItemIndex, getChangeName.getText());
                }
                if (!getChangeAmount.getText().equals("")) {
                    currentBudget.changeExpenseAmt(currentItemIndex,
                            Math.round(Double.parseDouble(getChangeAmount.getText()) * 100) / 100.0);
                }
            }
            itemOptionPanel.remove(changeItemPanel);
            reInitItemOptions();
        } else {
            itemAlertMessage.setText("Please enter a non-existing name and a positive number");
        }
    }

    //MODIFIES: this
    //EFFECTS: returns true if given name doesn't exist in income or expenses depending on the item
    private boolean changeItemName() {
        if (getChangeName.getText().equals("")) {
            return true;
        } else if (currentBudget.notInBudget(getChangeName.getText(), currentBudget.getIncome())
                && changingIncome) {
            return true;
        } else if (currentBudget.notInBudget(getChangeName.getText(), currentBudget.getExpenses())
                && changingExpense) {
            return true;
        } else {
            return false;
        }
    }

    //MODIFIES: this
    //EFFECTS: returns true if input can be converted to non-negative double, false otherwise
    private boolean changeItemAmount() {
        try {
            if (getChangeAmount.getText().equals("")) {
                return true;
            }
            double newAmount = Math.round(Double.parseDouble(getChangeAmount.getText()) * 100.0) / 100.0;
            if (newAmount <= 0) {
                return false;
            } else {
                return true;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void executeChangeItem(int index) {
        int incSize = currentBudget.getIncome().size();
        if (index >= incSize) {
            //currentItem = currentBudget.getExpenses().get(index - incSize);
            changingExpense = true;
            currentItemIndex = index - incSize;
        } else {
            //currentItem = currentBudget.getIncome().get(index);
            changingIncome = true;
            currentItemIndex = index;
        }
        clearItemOptions();
        itemOptionPanel.add(changeItemPanel);
        itemAlertMessage.setText("Enter the new Name and/or Amount (Leave empty to keep the same):");
        update(getGraphics());
    }

    //MODIFIES: this
    //EFFECTS: removed an item from a Budget
    private void killItem(int toDelete) {
        int incSize = currentBudget.getIncome().size();
        if (toDelete >= incSize) {
            currentBudget.deleteExpense(toDelete - incSize);
        } else {
            currentBudget.deleteIncome(toDelete);
        }
        itemOptionPanel.remove(delItemPanel);
        reInitItemOptions();
    }

    //MODIFIES: this
    //EFFECTS: returns app to main menu from budget menu
    private void returnToMainFromBgt() {
        currentBudget = null;
        remove(oneBgtPanel);
        remove(itemOptionPanel);
        oneBgtPanel.removeAll();
        unDrawBudgets();
        drawBudgets();
        add(titlePanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);
        alertMessage.setText("Select an option below, or a Budget to view it");
        setVisible(true);
        update(getGraphics());
    }

    //MODIFIES: this
    //EFFECTS: adds an item to a budget if string is not empty and a positive number is given
    private void attemptAddToInc() {
        if (getAddIncName.getText().equals("")) {
            itemAlertMessage.setText("You must choose a name!");
        } else {
            try {
                double amount = Double.parseDouble(getAddIncAmount.getText());
                double amountRounded = Math.round(amount * 100.0) / 100.0;
                if (currentBudget.notInBudget(getAddIncName.getText(), currentBudget.getIncome())) {
                    currentBudget.addItem(amountRounded, getAddIncName.getText(), 0);
                    refreshBgt();
                } else {
                    itemAlertMessage.setText("That name already exists in the list!");
                }
            } catch (NumberFormatException | NegativeAmountException e) {
                itemAlertMessage.setText("You must enter a positive number!");
            } finally {
                update(getGraphics());
            }
        }
    }

    //MODIFIES: this
    //EFFECTS: adds an item to a budget if string is not empty and a positive number is given
    private void attemptAddToExp() {
        if (getAddExpName.getText().equals("")) {
            itemAlertMessage.setText("You must choose a name!");
        } else {
            try {
                double amount = Double.parseDouble(getAddExpAmount.getText());
                double amountRounded = Math.round(amount * 100.0) / 100.0;
                if (currentBudget.notInBudget(getAddExpName.getText(), currentBudget.getExpenses())) {
                    currentBudget.addItem(amountRounded, getAddExpName.getText(), 1);
                    refreshBgt();
                } else {
                    itemAlertMessage.setText("That name already exists in the list!");
                }
            } catch (NumberFormatException | NegativeAmountException e) {
                itemAlertMessage.setText("You must enter a positive number!");
            } finally {
                update(getGraphics());
            }
        }
    }


    //MODIFIES: this
    //EFFECTS: allows user to change the Budget name
    private void executeNameChange() {
        clearItemOptions();
        itemOptionPanel.add(changeNamePanel);
        itemAlertMessage.setText("Enter the new Budget name (Submit nothing to cancel):");
        update(getGraphics());
    }

    //MODIFIES: this
    //EFFECTS: changes a budget's name based on the user inputs
    private void changeBudgetName() {
        String newName = newBgtName.getText();
        boolean valid = true;
        for (Budget bgt: budgetList.getBudgets()) {
            if (newName.equals(bgt.getName())) {
                valid = false;
            }
        }
        if (newName.equals("")) {
            itemOptionPanel.remove(changeNamePanel);
            reInitItemOptions();
        } else if (!valid) {
            itemAlertMessage.setText("Another Budget has that name!");
        } else if (valid) {
            currentBudget.changeName(newName);
            itemOptionPanel.remove(changeNamePanel);
            reInitItemOptions();
        }
    }

    //MODIFIES: this
    //EFFECTS: adds item deletion interface
    private void executeItemDelete() {
        deletingItems = true;
        clearItemOptions();
        itemAlertMessage.setText("Click on the item you wish to delete: ");
        itemOptionPanel.add(delItemPanel);
        update(getGraphics());
    }

    //MODIFIES: this
    //EFFECTS: draws back item options on single budget GUI
    private void reInitItemOptions() {
        itemOptionPanel.add(addIncMessage);
        itemOptionPanel.add(addIncPanel);
        itemOptionPanel.add(addExpMessage);
        itemOptionPanel.add(addExpPanel);
        itemOptionPanel.add(itemAlertMessage);
        itemOptionPanel.add(otherItemOptionsPanel);
        deletingItems = false;
        changingIncome = false;
        changingExpense = false;
        refreshBgt();
    }

    //MODIFIES: this
    //EFFECTS: refreshes the budget overview
    private void refreshBgt() {
        itemListPanel.remove(incomeListPanel);
        itemListPanel.remove(expenseListPanel);
        oneBgtPanel.remove(itemListPanel);
        drawItems();
        getAddIncName.setText("");
        getAddIncAmount.setText("");
        getAddExpName.setText("");
        getAddExpAmount.setText("");
        getChangeName.setText("");
        getChangeAmount.setText("");
        currentItemIndex = 0;
        itemAlertMessage.setText("Or choose an option below:");
        oneBgtPanel.add(itemListPanel, BorderLayout.CENTER);
        setVisible(true);
        update(getGraphics());
    }

    //MODIFIES: this
    //EFFECTS: removes main budget item interface
    private void clearItemOptions() {
        itemOptionPanel.remove(addIncMessage);
        itemOptionPanel.remove(addIncPanel);
        itemOptionPanel.remove(addExpMessage);
        itemOptionPanel.remove(addExpPanel);
        itemOptionPanel.remove(otherItemOptionsPanel);
    }




























































    //MODIFIES: this
    //EFFECTS: shows user input options, and takes user input
    private void runBudgetApp() {
        boolean running = true;
        String userInput;

        setUp();

        while (running) {
            showOptions();
            userInput = input.next();
            userInput = userInput.toLowerCase();

            if (userInput.equals("q")) {
                running = false;
            } else {
                viewBudgets(userInput);
            }
        }

        System.out.println("\nShutting down BudgetApp...");
    }

    //MODIFIES: this
    //EFFECTS: sets up budgets
    private void setUp() {
        //budgetList = new ArrayList<Budget>(); OLD CODE
        budgetList = new BudgetList();
        //budgetList.add(new Budget(0, "New Budget")); OLD CODE
        input = new Scanner(System.in);
    }

    //MODIFIES: this
    //EFFECTS: Allows user to view or select a budget
    private void viewBudgets(String userInput) {
        if (userInput.equals("v")) {
            printAllBudgets();
        } else if (userInput.equals("a")) {
            addNewBudget();
        } else if (userInput.equals("d")) {
            deleteBudget();
        } else if (userInput.equals("s")) {
            System.out.println("Which budget? (Select the Budget number): ");
            printAllBudgets();
            String toView = input.next();
            int view = Integer.parseInt(toView);
            //viewBudget(budgetList.get(view - 1)); OLD CODE
            viewBudget(budgetList.giveBudget(view - 1));
        } else if (userInput.equals("k")) {
            saveBudgetList();
        } else if (userInput.equals("l")) {
            loadBudgetList();
        } else {
            System.out.println("INVALID INPUT");
        }
    }

    //EFFECTS: Prints a list of all Budgets created
    private void printAllBudgets() {
        int counter = 0;
        for (int index = 0; index < budgetList.getSize(); index++) {
            Budget cbudget = budgetList.giveBudget(index);
            //Budget cbudget = budgetList.get(index); OLD CODE
            System.out.print((index + 1) + " - " + "Name: " + cbudget.getName() + ", ");
            if (cbudget.getFrequency()) {
                System.out.print("Type: Yearly\n");
            } else {
                System.out.print("Type: Monthly\n");
            }
            counter++;
        }
        if (counter == 0) {
            System.out.println("No budgets right now. Maybe add one?");
        }
        System.out.println("");
    }

    //EFFECTS: Displays options for main menu
    private void showOptions() {
        System.out.println("Please choose a command you wish to execute:");
        System.out.println("\"v\" to view all budgets");
        System.out.println("\"s\" to select a budget to view");
        System.out.println("\"a\" to add a new budget");
        System.out.println("\"s\" to delete a budget");
        System.out.println("\"k\" to save budget list to file");
        System.out.println("\"l\" to load budget list from file");
        System.out.println("\"q\" to close application");
    }

    //MODIFIES: this
    //EFFECTS: adds new budget to the list of Budgets
    private void addNewBudget() {
        System.out.print("Enter the name of your new Budget: ");
        Scanner nom = new Scanner(System.in);
        String name = nom.nextLine();
        if (name.equals("")) {
            name = "New Budget";
        }
        System.out.print("Enter timeline of your new Budget ");
        System.out.println("(0 or \"Monthly\" = Monthly, 1 or \"Yearly\" = Yearly)): ");
        String type = input.next();
        type = type.toLowerCase();
        if (type.equals("0") || type.equals("monthly")) {
            budgetList.addBudget(new Budget(0, name));
            //budgetList.add(new Budget(0, name)); OLD CODE
            System.out.println("Budget successfully added.\n");
        } else if (type.equals("1") || type.equals("yearly")) {
            budgetList.addBudget(new Budget(1, name));
            //budgetList.add(new Budget(1, name));
            System.out.println("Budget successfully added.\n");
        } else {
            System.out.println("INVALID INPUT\n");
        }
    }

    //MODIFIES: this
    //EFFECTS: deletes a budget
    private void deleteBudget() {
        System.out.println("Please select the number of the budget you would like to delete:");
        printAllBudgets();
        String toDelete = input.next();
        int delete = Integer.parseInt(toDelete);
        if (delete <= budgetList.getSize()) {
            //budgetList.remove(delete - 1); OLD CODE
            budgetList.delBudget(delete - 1);
            System.out.println("Budget " + delete + " successfully deleted.\n");
        } else {
            System.out.println("INVALID INPUT\n");
        }
    }

    //MODIFIES: this
    //EFFECTS: takes user input on viewing or editing a budget
    private void viewBudget(Budget bgt) {
        boolean viewing = true;
        String userInput;

        while (viewing) {
            showBgtOptions();
            userInput = input.next();
            userInput = userInput.toLowerCase();

            if (userInput.equals("q")) {
                viewing = false;
            } else {
                processBudget(userInput, bgt);
            }
        }
    }

    //EFFECTS: Shows options for budgets
    private void showBgtOptions() {
        System.out.println("Please choose a command you wish to execute:");
        System.out.println("\"v\" to get an overview");
        System.out.println("\"b\" to change the budget name");
        System.out.println("\"a\" to add an income or expense");
        System.out.println("\"d\" to delete an income or expense");
        System.out.println("\"c\" to change an income or expense");
        System.out.println("\"x\" to change the budget type WITHOUT scaling amounts");
        System.out.println("\"y\" to change the budget type AND scale amounts");
        System.out.println("\"q\" to return to main menu");
    }

    //MODIFIES: bgt
    //EFFECTS: processes commands for a budget
    private void processBudget(String userInput, Budget bgt) {
        if (userInput.equals("v")) {
            getBudgetOverview(bgt);
        } else if (userInput.equals("a")) {
            addToBudget(bgt);
        } else if (userInput.equals("b")) {
            changeBgtName(bgt);
        } else if (userInput.equals("c")) {
            changeBgtItem(bgt);
        } else if (userInput.equals("d")) {
            deleteBgtItem(bgt);
        } else if (userInput.equals("x")) {
            changeTimeNotAmount(bgt);
        } else if (userInput.equals("y")) {
            changeTimeAndAmount(bgt);
        } else {
            System.out.println("INVALID INPUT");
        }
    }


    //EFFECTS: displays overview of bgt
    private void getBudgetOverview(Budget bgt) {
        double expenses = Math.round(bgt.getTotalExpenses() * 100.0) / 100.0;
        double income = Math.round(bgt.getTotalIncome() * 100.0) / 100.0;
        double netAmt = income - expenses;
        boolean bgtType = bgt.getFrequency();
        if (bgtType) {
            System.out.print("Yearly ");
        } else {
            System.out.print("Monthly ");
        }
        netAmt = Math.round(netAmt * 100.0) / 100.0;
        System.out.println("Budget \"" + bgt.getName() + "\" Overview:");
        System.out.println("Surplus/Deficit: $" + netAmt);
        System.out.println("Total Income: $" + income);
        System.out.println("Total Expenses: $" + expenses);
        if (netAmt < 0) {
            System.out.println("ALERT: BUDGET RUNS A DEFICIT.");
        } else if (netAmt < income / 10.0) {
            System.out.println("ALERT: LESS THAN 10% OF BUDGET REMAINING. It is suggested to save 10% of your income.");
        }
        displayIncome(bgt);
        displayExpenses(bgt);
        System.out.println("");
    }

    //EFFECTS: Displays income of the selected budget
    private void displayIncome(Budget bgt) {
        System.out.println("Income:");
        if (bgt.getIncome().size() == 0) {
            System.out.println("No income yet. Maybe add one?");
        }
        for (int index = 0; index < bgt.getIncome().size(); index++) {
            Item item = bgt.getIncome().get(index);
            double amt = Math.round(item.getAmount() * 100.0) / 100.0;
            String name = item.getTitle();
            System.out.println((index + 1) + " - Amount: $" + amt + ", Name: " + name);
        }
    }

    //EFFECTS: Displays expenses of the selected budget
    private void displayExpenses(Budget bgt) {
        System.out.println("Expenses:");
        if (bgt.getExpenses().size() == 0) {
            System.out.println("No expenses yet. Maybe add one?");
        }
        for (int index = 0; index < bgt.getExpenses().size(); index++) {
            Item item = bgt.getExpenses().get(index);
            double amt = Math.round(item.getAmount() * 100.0) / 100.0;
            String name = item.getTitle();
            System.out.println((index + 1) + " - Amount: $" + amt + ", Name: " + name);
        }
    }

    //MODIFIES: bgt
    //EFFECTS: Changes name of bgt
    private void changeBgtName(Budget bgt) {
        Scanner priInput = new Scanner(System.in);
        System.out.print("Enter the new name: ");
        String oldName = bgt.getName();
        String newBgtName = priInput.nextLine();
        bgt.changeName(newBgtName);
        System.out.println("Budget name changed from \"" + oldName + "\" to \"" + newBgtName + "\"\n");
    }

    //MODIFIES: bgt
    //EFFECTS: Adds a new income or expense to bgt
    private void addToBudget(Budget bgt) {
        System.out.println("Add income or expense? (0 for income, 1 for expense)");
        String choose = input.next();
        Scanner priInput = new Scanner(System.in);
        int choice = 1;
        int valid = 1;
        if (choose.equals("0")) {
            choice = 0;
        } else if (!choose.equals("1")) {
            System.out.println("INVALID INPUT\n");
            valid = 0;
        }
        if (valid == 1) {
            System.out.print("Enter the name of the new Item: ");
            String name = priInput.nextLine();
            System.out.print("Enter the amount: $");
            double amount = Math.round(input.nextDouble() * 100.0) / 100.0;
            tryAdd(amount, name, choice, bgt);

        }
    }

    //MODIFIES: this
    //EFFECTS: adds an item to budget if name and amount are valid
    private void tryAdd(double amount, String name, int choice, Budget bgt) {
        try {
            if (bgt.addItem(amount, name, choice)) {
                System.out.println("Item successfully added!\n");
            } else {
                System.out.println("Item with given name already in the same category, or amount is below zero.\n");
            }
        } catch (NegativeAmountException e) {
            System.err.println("Amount must be non-negative!");
        }
    }


    //MODIFIES: bgt
    //EFFECTS: changes an item's price or name
    public void changeBgtItem(Budget bgt) {
        System.out.print("Change an income or expense? (0 for Income, 1 for Expense): ");
        String choice = input.next();
        if (choice.equals("0")) {
            if (bgt.getIncome().size() != 0) {
                selectIncomeItem(bgt);
            } else {
                System.out.println("No Income exists yet, so you can't change an income.\n");
            }
        } else if (choice.equals("1")) {
            if (bgt.getExpenses().size() != 0) {
                selectExpenseItem(bgt);
            } else {
                System.out.println("No Expense exists yet, so you can't change an expense.\n");
            }
        } else {
            System.out.println("INVALID INPUT\n");
        }
    }

    //MODIFIES: bgt
    //EFFECTS: makes sure Item # given is within income list
    public void selectIncomeItem(Budget bgt) {
        displayIncome(bgt);
        System.out.print("Enter the ID # of the expense you wish to change: ");
        int num = input.nextInt();
        if (num <= bgt.getIncome().size() && num > 0) {
            changeIncomeItem(bgt, num - 1);
        } else {
            System.out.println("Income with given ID does not exist.\n");
        }
    }

    //MODIFIES: bgt
    //EFFECTS: makes sure Item # given is within expense list
    public void selectExpenseItem(Budget bgt) {
        displayExpenses(bgt);
        System.out.print("Enter the ID # of the expense you wish to change: ");
        int num = input.nextInt();
        if (num <= bgt.getIncome().size() && num > 0) {
            changeExpenseItem(bgt, num - 1);
        } else {
            System.out.println("Expense with given ID does not exist.");
        }
    }

    //MODIFIES: item
    //EFFECTS: changes the amount or name of income
    private void changeIncomeItem(Budget bgt, int index) {
        System.out.print("Change name or amount? (0 or \"Name\" for Name, 1 or \"Amount\" for Amount): ");
        String choice = input.next();
        Scanner priInput = new Scanner(System.in);
        choice = choice.toLowerCase();
        if (choice.equals("0") || choice.equals("name")) {
            System.out.print("Enter the new name: ");
            String newItemName = priInput.nextLine();
            if (bgt.changeIncomeName(index, newItemName)) {
                System.out.println("Income name successfully changed.\n");
            } else {
                System.out.println("Income with identical name already exists.\n");
            }
        } else if (choice.equals("1") || choice.equals("amount")) {
            System.out.print("Enter the new amount: $");
            double newItemAmt = Math.round(input.nextDouble() * 100.0) / 100.0;
            if (bgt.changeIncomeAmt(index, newItemAmt)) {
                System.out.println("Income amount successfully changed.\n");
            } else {
                System.out.println("NEW AMOUNT MUST BE AT LEAST ZERO.\n");
            }
        } else {
            System.out.println("INVALID INPUT\n");
        }
    }

    //MODIFIES: item
    //EFFECTS: changes the amount or name of expense
    private void changeExpenseItem(Budget bgt, int index) {
        System.out.print("Change name or amount? (0 or \"Name\" for Name, 1 or \"Amount\" for Amount): ");
        String choice = input.next();
        choice = choice.toLowerCase();
        Scanner priInput = new Scanner(System.in);
        if (choice.equals("0") || choice.equals("name")) {
            System.out.print("Enter the new name: ");
            String newItemName = priInput.nextLine();
            if (bgt.changeExpenseName(index, newItemName)) {
                System.out.println("Expense name successfully changed.\n");
            } else {
                System.out.println("Expense with identical name already exists.\n");
            }
        } else if (choice.equals("1") || choice.equals("amount")) {
            System.out.print("Enter the new amount: $");
            double newItemAmt = Math.round(input.nextDouble() * 100.0) / 100.0;
            if (bgt.changeExpenseAmt(index, newItemAmt)) {
                System.out.println("Expense amount successfully changed.\n");
            } else {
                System.out.println("NEW AMOUNT MUST BE AT LEAST ZERO.\n");
            }
        } else {
            System.out.println("INVALID INPUT\n");
        }
    }

    //MODIFIES: this
    //EFFECTS: deletes an Item from bgt
    private void deleteBgtItem(Budget bgt) {
        System.out.print("Delete an Income or an Expense? (0 for Income, 1 for Expense): ");
        String choice = input.next();
        if (choice.equals("0")) {
            if (bgt.getIncome().size() != 0) {
                deleteIncomeItem(bgt);
            } else {
                System.out.println("No income exists, so there is nothing to delete.\n");
            }
        } else if (choice.equals("1")) {
            if (bgt.getExpenses().size() != 0) {
                deleteExpenseItem(bgt);
            } else {
                System.out.println("No expense exists, so there is nothing to delete.\n");
            }
        } else {
            System.out.println("INVALID INPUT");
        }
    }

    //MODIFIES: bgt
    //EFFECTS: deletes an income from bgt
    private void deleteIncomeItem(Budget bgt) {
        displayIncome(bgt);
        System.out.print("Enter the ID # of the Income you wish to delete: ");
        int choice = input.nextInt();
        if (choice > 0 && choice <= bgt.getIncome().size()) {
            bgt.deleteIncome(choice - 1);
            System.out.println("Income deleted successfully.\n");
        } else {
            System.out.println("Income with given ID does not exist.\n");
        }
    }

    //MODIFIES: bgt
    //EFFECTS: deletes an expense from bgt
    private void deleteExpenseItem(Budget bgt) {
        displayExpenses(bgt);
        System.out.print("Enter the ID # of the Expense you wish to delete: ");
        int choice = input.nextInt();
        if (choice > 0 && choice <= bgt.getExpenses().size()) {
            bgt.deleteExpense(choice - 1);
            System.out.println("Expense deleted successfully.\n");
        } else {
            System.out.println("Expense with given ID does not exist.\n");
        }
    }

    //MODIFIES: bgt
    //EFFECTS: changed bgt from Monthly to Yearly, or vice versa, without changing any amount values
    private void changeTimeNotAmount(Budget bgt) {
        bgt.changeTimePeriod();
        if (bgt.getFrequency()) {
            System.out.println("Budget changed from Monthly to Yearly.\n");
        } else {
            System.out.println("Budget changed from Yearly to Monthly.\n");
        }
    }

    //MODIFIES: bgt
    //EFFECTS: changed bgt from Monthly to Yearly, or vice versa, and scales all amount values
    private void changeTimeAndAmount(Budget bgt) {
        bgt.changeTimePeriodAndAmounts();
        if (bgt.getFrequency()) {
            System.out.println("Budget changed from Monthly to Yearly, and multiplied all amounts by 12 to scale.\n");
        } else {
            System.out.println("Budget changed from Yearly to Monthly, and divided all amounts by 12 to scale.\n");
        }
    }

    // EFFECTS: saves budgetList to file
    private void saveBudgetList() {
        try {
            writer.open();
            writer.write(budgetList);
            writer.close();
            System.out.println("Saved all your budgets to " + JSON_FILE);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to write to file: " + JSON_FILE);
        }
    }

    // MODIFIES: this
    // EFFECTS: loads budgetList from file
    private void loadBudgetList() {
        try {
            budgetList = reader.read();
            System.out.println("Loaded all your Budgets from " + JSON_FILE);
        } catch (IOException e) {
            System.out.println("Unable to read from file: " + JSON_FILE);
        }
    }







    //THE UNNEEDED FUNCTION OF SHAME THAT I KEEP HERE JUST IN CASE


    //MODIFIES: this
    //EFFECTS: declares and instantiates all modes for the main menu
    /*
    private void addOptions() {
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(0, 1));
        bottomPanel.getSize(new Dimension(0,0));
        optionPanel = new JPanel();
        alertMessage = new JLabel("Select an option below, or a Budget to view it", SwingConstants.CENTER);
        bottomPanel.add(alertMessage);
        optionSetUp();
        bottomPanel.add(optionPanel);
    }
    */

}

