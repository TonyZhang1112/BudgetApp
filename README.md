# My Personal Project

## Budget Maker/Manager

For my personal CPSC 210 project, I will be making an application that allows the user to create (multiple) budgets, and will **organize them and keep track of earnings and spendings**. It will also suggest amounts to save, and should alert when spending gets close to, or over, the user's income. This project is of interest to me because it would allow me to create something I could use (and personalize) for myself in the future.

This application will be useful for:
- Those who need to manage multiple spending budgets (Ex. Accountants)
- Those who have trouble balancing their own budget
- Those who have many income and expenses to manage

## User stories:
- As a user, I want to be able to add multiple Budgets to a BudgetList
- As a user, I want to be able to delete Budgets
- As a user, I want to be able to view a list of all my Budgets
- As a user, I want to be able to add multiple Items to a Budget
- As a user, I want to be able to delete multiple Items from a Budget
- As a user, I want to be able to view a complete summary of a Budget, including time period, name, surplus/deficit, total income and expenses, and each individual income and expense
- As a user, I want to be able to make a Budget Monthly or Yearly
- As a user, I want to be able to change the time period my my Budget, and for the application to recalculate the totals for me (if I want it to)
- As a user, I want to be able to change details of Budget Items such as its name and amount
- As a user, I want to be able to save my Budgets with all their items
- As a user, I want to be able to load my Budgets with all their items


## Phase 4: Task 2
For Task 2, I chose to make a class (Budget) Robust by making an exception for non-positive numbers being used in Item amounts. The methods involced are addItem in the Budget class, and all the methods in BudgetApp that call AddItem (Ex. attemptAddToInc and attemptAddToExp).


## Phase 4: Task 3
Given all the features of my program and the amount of classes shown in my UML Class Diagram, it's clear that my codee doesn't adhere to the "One Class, one Responsibility" Rule.Some of the refactoring I would do would be to:
- Create a Tool Class for all the different operations
- Create classes that would handle all the different features individually (add items, delete them, etc.) that would also extend the Tool Class
- Refactor methods to do with adding income/expenses (Items) to reduce the amount of repetition between them, as operations on income and expenses were usually seperate