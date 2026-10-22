Java Expense Tracker

A menu-driven Java console application to record expenses and track spending. Your data is saved to a CSV file, so it is still there the next time you run the program.

Features
Add an expense with a description, amount, category and date
View all expenses in a table, newest first
Show total spending
Category-wise spending summary
Monthly summary with the number of transactions
Update or delete an expense by its ID
Input validation for amounts, dates and empty fields
Automatic saving to data/expenses.csv
Tech Stack
Java 17 or higher
Collections: ArrayList, LinkedHashMap
Streams and lambda expressions
BigDecimal for accurate money calculations
java.time (LocalDate, YearMonth) for dates
File I/O (java.nio.file) for CSV storage
Project Structure
java-expense-tracker/
├── Expense.java          # Expense model (fields, getters, CSV conversion)
├── ExpenseTracker.java   # Main program: menu and all operations
├── data/
│   └── expenses.csv      # Created automatically when you save
└── README.md
How to Run
Install JDK 17 or higher and check it with java -version.
Clone the repository:
   git clone https://github.com/Nikitha032/java-expense-tracker.git
   cd java-expense-tracker
Compile:
   javac -d out Expense.java ExpenseTracker.java
Run:
   java -cp out com.nikki.expensetracker.ExpenseTracker
Menu
1. Add expense
2. View all expenses
3. Show total spending
4. Category summary
5. Update expense
6. Delete expense
7. Monthly summary
0. Save and exit

Choose 0 to save and exit. Dates use the format YYYY-MM-DD, and the monthly summary uses YYYY-MM. Leave the date blank to use today.

What I Learned
Designing a program with separate classes for the data (Expense) and the logic (ExpenseTracker)
Using collections and streams to filter, sort and group data
Why BigDecimal is used for money instead of double
Reading and writing files, and handling errors without crashing
Validating user input
Future Improvements
Convert it to a Spring Boot REST API
Store data in a MySQL database instead of a CSV file
Add a web frontend with HTML, CSS and JavaScript
Add user login
Add unit tests
