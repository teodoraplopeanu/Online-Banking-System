
**Name: Teodora-Anca Plopeanu**
**Group: 322CA**

# J. OOP Morgan Chase & Co.

## Project Overview

* This project implements an online banking system in **Java**, showcasing core functionalities of a modern bank. Key features include account management, currency exchange, transactions (with commission and cashback), split payments and detailed reporting.
* The project follows the key principles of **object-oriented programming (OOP)** and employs a **modular architecture** to facilitate scalability and maintainability. Also makes use of various design patterns.


## Structure

The `main` package contains the essential classes, that communicate indirectly, through the Main class, with the i/o classes.
	`main` includes:
	
* **EBank** - represents the actual bank. It is implemented by using the Singleton pattern, so its properties are reset at the beginning of each test. Includes basic functions for processing input specific to the class, as well as methods that help find the exchange between two currencies, by using the Floyd-Warshall algorithm. Also contains hashmaps that keep evidence of the correspondence between userEmail and User classes or IBANs and Accounts/Business Accounts (in order to differentiate approach at certain commands), as well as a list of the current split payments which are still waiting for approval from the accounts involved.

* `reset` package, which contains classes that reset the EBank data after each test.
	* **ResetEBank** - visitor that resets the fields of the EBank, at the end of every test. Implemented by using the **Visitor** and **Visitable** interfaces.

* **ExchangeRate** - contains fields specific to an exchange rate (from and to currency) and the rate (double).

* **User** - represents a user of the bank. Has identification fields, like firstName, lastName or email. A user may have multiple accounts and cards associated with them.  So, the class contains a list with all those accounts, as well as a series of aliases. It also contains a list with all the transactions done by the user.

*  **Account** - contains all the data specific to an account, for instance IBAN, currency, type, minimum balance, interest rate. Also contains a reference to the owner of the account, the current balance and a list with all the cards associated with the account. Stores commerciant data in a TreeMap (so as to be sorted in alphabetical order), as well as a list with all the transactions done through the account. Also contains transaction statistics, which are used at giving cashback (totalSpendingThreshold, transactionsCount, nrOfTransactionsStatistics).

* **Card** - contains the cardNumber (String), the cardType (normal / one time pay) and the status (which can be one of the following: "active", "frozen", "warning", or "deleted").

* **Commerciant** - contains specific data for a commerciant (like name, id, account and cashback strategy).

* **Transaction** - class used for generating a transaction output/ObjectNode after each command. Implemented by using the **Builder** design pattern, because each type of command leads to a specific type of transaction.

* **CustomSplitPayment** - class used to initiate a split payment, which will be then stored in the EBank, until all the involved accounts accept it (and then it starts execution) or one rejects it (and it is deleted).

* `cashback` package:
		* **CashbackFactory** - returns the corresponding cashback class, according to the type given as a string
		* **CashbackStrategy** - interface used for implementing different types of cashback strategies. Contains *giveCashback* method
		* **NrOfTransactions** and **SpendingThreshold** - available cashback strategies
	
* `commission` package:
	 * **CommissionFactory** - returns the corresponding commission class, according to the type given as a string
	* **CommissionStrategy** - interface used for implementing different types of commission strategies. Contains *giveCashback* method
	* **Standard**, **Student**, **Silver** and **Gold** - available commission strategies

* `statistics` package - contains classes used for statistics (CommerciantStatistics, NrOfTransactionsStatistics)

* `commands` package contains:
	* **Command** - abstract class that is extended by all the others. Contains only one method: *execute*, which returns an ObjectNode - the result/output of the executed command. Implemented by useing the **Command** design pattern.
	* **CommandFactory** - a factory that returns the specific class corresponding to the command that has to be executed.	
	* **Available commands**: 
		* `bankingData` package:
			* `account`: *AddAccount, AddBusinessAccount, DeleteAccount, AddNewBusinessAssociate, ChangeDepositLimit, ChangeSpendingLimit, ChangeInterestRate, SetAlias, SetMinBalance, UpgradePlan*;
			* `card`: *AddCard, DeleteCard, CheckCardStatus*;
		* `moneyTransfer`: *PayOnline, SendMoney, cashWithdrawal, WithdrawSavings, AddFunds, AddInterest, SplitPayment, AcceptSplitPayment, RejectSplitPayment*;
		* `reports`: *BusinessReport, SpendingsReport, Report, PrintUsers, PrintTransactions*.
		
* `exceptions` :
	* **Exception classes** - redefined exceptions, useful for the project, such as *InsufficientFundsException* or *InvalidUserexception*.

* `utils`: contains constants used during the program


## Flow

1. The system starts by loading user data, exchange rates, and merchant categories from provided files.
2. Users register and manage their personal information, accounts, cards, and aliases within the system.
3. Account operations include creating, funding, and maintaining savings or classic accounts with flexible features.
4. Users manage cards, such as standard or one-time-pay options, to facilitate secure transactions.
5. Transaction processing supports payments, transfers, and detailed reporting, ensuring accuracy and error handling.


## OOP Concepts Used

* **Encapsulation** : Class properties are kept private and accessed externally through dedicated getters and setters, ensuring controlled interaction with the class data.

* **Inheritance** : Classes extend base functionality, such as the abstract class `Command` being the extended by all the other commands, or Visitor interface implemented by ResetVisitor.

* **Polymorphism** : Methods are overridden or overloaded to implement unique behaviors, such as superpowers for different characters.
* **Abstractisation** - use of abstract classes and interfaces.
* **Design Patterns**:
	* 3 x Factory (for commands, commission and cashback)
	* 2 x Strategy (for commission and cashback)
	* Visitor (for resetting the EBank)
	* Builder (for transactions)
	* Singleton (for EBank)
	* Command (for commands)
