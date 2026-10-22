package com.nikki.expensetracker;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ExpenseTracker {
    private static final Path DATA_FILE = Path.of("data", "expenses.csv");
    private final List<Expense> expenses = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);
    private int nextId = 1;

    public static void main(String[] args) {
        new ExpenseTracker().run();
    }

    private void run() {
        loadExpenses();
        System.out.println("\n====================================");
        System.out.println("      PERSONAL EXPENSE TRACKER");
        System.out.println("====================================");
        boolean running = true;
        while (running) {
            printMenu();
            switch (readInt("Choose an option: ", 0, 7)) {
                case 1 -> addExpense();
                case 2 -> listExpenses();
                case 3 -> showTotal();
                case 4 -> showCategorySummary();
                case 5 -> updateExpense();
                case 6 -> deleteExpense();
                case 7 -> showMonthlySummary();
                case 0 -> { saveExpenses(); running = false; System.out.println("Your expenses are saved. Goodbye!"); }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n1. Add expense\n2. View all expenses\n3. Show total spending\n4. Category summary\n5. Update expense\n6. Delete expense\n7. Monthly summary\n0. Save and exit");
    }

    private void addExpense() {
        String description = readText("Description: ");
        BigDecimal amount = readAmount("Amount (INR): ");
        String category = readText("Category (e.g. Food, Travel, Bills): ");
        LocalDate date = readDate("Date (YYYY-MM-DD, blank for today): ", true);
        Expense expense = new Expense(nextId++, description, amount, category, date);
        expenses.add(expense);
if (saveExpenses()) {System.out.println("Expense added successfully. ID: " + expense.getId());
} else {expenses.remove(expense);
    System.out.println("Expense could not be saved.");
}
    }

    private void listExpenses() {
        if (expenses.isEmpty()) { System.out.println("No expenses recorded yet."); return; }
        printTable(expenses.stream().sorted(Comparator.comparing(Expense::getDate).reversed().thenComparing(Expense::getId)).toList());
    }

    private void showTotal() {
        BigDecimal total = expenses.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("Total recorded spending: " + money(total));
    }

    private void showCategorySummary() {
        if (expenses.isEmpty()) { System.out.println("No expenses to summarize."); return; }
        Map<String, BigDecimal> totals = new LinkedHashMap<>();
        expenses.stream().sorted(Comparator.comparing(Expense::getCategory, String.CASE_INSENSITIVE_ORDER)).forEach(e -> totals.merge(e.getCategory(), e.getAmount(), BigDecimal::add));
        System.out.println("\nSpending by category:");
        totals.forEach((category, total) -> System.out.printf("%-20s %s%n", category, money(total)));
    }

    private void updateExpense() {
        Expense old = findById(readInt("Expense ID to update: ", 1, Integer.MAX_VALUE));
        if (old == null) { System.out.println("No expense found with that ID."); return; }
        System.out.println("Press Enter to keep the current value.");
        String description = readOptional("Description [" + old.getDescription() + "]: ", old.getDescription());
        BigDecimal amount = readOptionalAmount("Amount [" + old.getAmount().toPlainString() + "]: ", old.getAmount());
        String category = readOptional("Category [" + old.getCategory() + "]: ", old.getCategory());
        LocalDate date = readOptionalDate("Date [" + old.getDate() + "]: ", old.getDate());
        Expense updated = new Expense(old.getId(), description, amount, category, date);
        int index = expenses.indexOf(old);
        expenses.set(index, updated);
        if (saveExpenses()) {
        System.out.println("Expense updated.");
        } else {
        expenses.set(index, old);
        System.out.println("Update could not be saved.");
        }
    }

    private void deleteExpense() {
        Expense expense = findById(readInt("Expense ID to delete: ", 1, Integer.MAX_VALUE));
        if (expense == null) { System.out.println("No expense found with that ID."); return; }
        if (readText("Delete '" + expense.getDescription() + "' for " + money(expense.getAmount()) + "? (yes/no): ").equalsIgnoreCase("yes")) {
            expenses.remove(expense);
        if (saveExpenses()) {
        System.out.println("Expense deleted.");
        } else {
        expenses.add(expense);
        System.out.println("Deletion could not be saved.");
        }
        } else System.out.println("Deletion cancelled.");
    }

    private void showMonthlySummary() {
        YearMonth month = readMonth("Month (YYYY-MM, blank for current month): ");
        List<Expense> matching = expenses.stream().filter(e -> YearMonth.from(e.getDate()).equals(month)).toList();
        BigDecimal total = matching.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("\nSummary for " + month + " — " + matching.size() + " transaction(s)");
        System.out.println("Total: " + money(total));
        if (!matching.isEmpty()) printTable(matching);
    }

    private Expense findById(int id) { return expenses.stream().filter(e -> e.getId() == id).findFirst().orElse(null); }
    private String readText(String prompt) { while (true) { System.out.print(prompt); String s = scanner.nextLine().trim(); if (!s.isEmpty()) return s; System.out.println("This field cannot be empty."); } }
    private String readOptional(String prompt, String current) { System.out.print(prompt); String s = scanner.nextLine().trim(); return s.isEmpty() ? current : s; }
    private BigDecimal readAmount(String prompt) { while (true) { System.out.print(prompt); try { BigDecimal n = new BigDecimal(scanner.nextLine().trim()); if (n.compareTo(BigDecimal.ZERO) > 0) return n; } catch (NumberFormatException ignored) { } System.out.println("Enter a valid amount greater than zero, e.g. 125.50."); } }
    private BigDecimal readOptionalAmount(String prompt, BigDecimal current) { while (true) { System.out.print(prompt); String s = scanner.nextLine().trim(); if (s.isEmpty()) return current; try { BigDecimal n = new BigDecimal(s); if (n.compareTo(BigDecimal.ZERO) > 0) return n; } catch (NumberFormatException ignored) { } System.out.println("Enter an amount greater than zero or press Enter to keep the current value."); } }
    private int readInt(String prompt, int min, int max) { while (true) { System.out.print(prompt); try { int n = Integer.parseInt(scanner.nextLine().trim()); if (n >= min && n <= max) return n; } catch (NumberFormatException ignored) { } System.out.println("Enter a whole number from " + min + " to " + max + "."); } }
    private LocalDate readDate(String prompt, boolean blankMeansToday) { while (true) { System.out.print(prompt); String s = scanner.nextLine().trim(); if (s.isEmpty() && blankMeansToday) return LocalDate.now(); try { return LocalDate.parse(s); } catch (DateTimeParseException ignored) { System.out.println("Use the format YYYY-MM-DD."); } } }
    private LocalDate readOptionalDate(String prompt, LocalDate current) { while (true) { System.out.print(prompt); String s = scanner.nextLine().trim(); if (s.isEmpty()) return current; try { return LocalDate.parse(s); } catch (DateTimeParseException ignored) { System.out.println("Use the format YYYY-MM-DD or press Enter to keep the current date."); } } }
    private YearMonth readMonth(String prompt) { while (true) { System.out.print(prompt); String s = scanner.nextLine().trim(); if (s.isEmpty()) return YearMonth.now(); try { return YearMonth.parse(s); } catch (DateTimeParseException ignored) { System.out.println("Use the format YYYY-MM."); } } }
    private String money(BigDecimal amount) { return "INR " + amount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString(); }

    private void printTable(List<Expense> items) {
        System.out.printf("%-5s %-25s %14s %-18s %-12s%n", "ID", "DESCRIPTION", "AMOUNT (INR)", "CATEGORY", "DATE");
        System.out.println("-------------------------------------------------------------------------------");
        for (Expense e : items) System.out.printf("%-5d %-25s %14s %-18s %-12s%n", e.getId(), clip(e.getDescription(), 25), e.getAmount().toPlainString(), clip(e.getCategory(), 18), e.getDate());
    }
    private String clip(String s, int length) { return s.length() <= length ? s : s.substring(0, length - 3) + "..."; }

    private void loadExpenses() {
        if (!Files.exists(DATA_FILE)) return;
        try {
            for (String line : Files.readAllLines(DATA_FILE)) {
                if (line.isBlank()) continue;
                try { Expense e = Expense.fromCsv(line); expenses.add(e); nextId = Math.max(nextId, e.getId() + 1); }
                catch (RuntimeException ex) { System.err.println("Skipping invalid saved record: " + line); }
            }
        } catch (IOException e) { System.err.println("Could not load saved expenses: " + e.getMessage()); }
    }
   private boolean saveExpenses() {
    try {
        Files.createDirectories(DATA_FILE.getParent());
        List<String> lines = expenses.stream().map(Expense::toCsv).toList();
        Files.write(DATA_FILE, lines);
        return true;
    } catch (IOException e) {System.err.println("Could not save expenses: " + e.getMessage());
        return false;
    }
}
}
