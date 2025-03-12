# Project Stage 2 - J. POO Morgan Chase & Co.

## Introduction
* This is an improved version of the last project with added functionality such as business accounts, cash back and commission

## Functionality
### 1. Basics
* Like last time we have users, accounts and cards, in adition we have business accounts
* The `Card` class now utilezes a *State* design pattern for better functionility for regular, frozen and warning states
* Creation of card states is done by a `CardStateFactory`
* *Command* pattern implemented in the previous stage facilitates the execution of new types of commands

### 2. Commands
* The creation and addition of new commands is now facilitated by a *Factory* design pattern

### 3. Transactions
* More types of transactions are now needed (`CashWithdrawl`, `WitdrawSavings`, etc.)
* In addition to `printTransaction`, `report` and `spendingsReport`, transactions are also needed for `businessReports`

### 4. Business accounts and reports
* Handling of business accounts is now done by the `businessAccount` class
* Associates have access to business accounts
* Assosicates are stored using the inner class `Associate` which includes:
  * type (employee / manager)
  * map of cards created by the associate
* Associates are stored using a map (key - email)
* `BusinessTransactionReport` and `BusinessCommercinatReport` use streams for efficient data processing
* This makes the implementation simpler and the code more readable.