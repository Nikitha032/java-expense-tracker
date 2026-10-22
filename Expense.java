package com.nikki.expensetracker;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public final class Expense {
    private final int id;
    private final String description;
    private final BigDecimal amount;
    private final String category;
    private final LocalDate date;

    public Expense(int id, String description, BigDecimal amount, String category, LocalDate date) {
        if (id <= 0) throw new IllegalArgumentException("ID must be positive.");
        if (description == null || description.isBlank()) throw new IllegalArgumentException("Description cannot be empty.");
        if (amount == null ||amount.compareTo(BigDecimal.ZERO) <= 0) { throw new IllegalArgumentException("Amount must be greater than zero." );
}

if (amount.stripTrailingZeros().scale() > 2) {
    throw new IllegalArgumentException(
        "Amount cannot have more than 2 decimal places."
    );
}
        if (category == null || category.isBlank()) throw new IllegalArgumentException("Category cannot be empty.");
        this.id = id;
        this.description = description.trim();
        this.amount = amount.setScale(2, java.math.RoundingMode.HALF_UP);
        this.category = category.trim();
        this.date = Objects.requireNonNull(date, "Date cannot be null.);
if (this.date.isAfter(LocalDate.now())) {throw new IllegalArgumentException("Expense date cannot be in the future." );
}
    }

    public int getId() { return id; }
    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
    public String getCategory() { return category; }
    public LocalDate getDate() { return date; }

    public String toCsv() {
        return id + "," + escape(description) + "," + amount.toPlainString() + "," + escape(category) + "," + date;
    }

    public static Expense fromCsv(String line) {
        String[] p = line.split(",", -1);
        if (p.length != 5) throw new IllegalArgumentException("Invalid expense record.");
        return new Expense(Integer.parseInt(p[0]), unescape(p[1]), new BigDecimal(p[2]), unescape(p[3]), LocalDate.parse(p[4]));
    }

    private static String escape(String value) { return value.replace("\\", "\\\\").replace(",", "\\c").replace("\n", "\\n").replace("\r", "\\r"); }
    private static String unescape(String value) {
        StringBuilder out = new StringBuilder(); boolean escaped = false;
        for (char c : value.toCharArray()) {
            if (escaped) { out.append(c == 'c' ? ',' : c == 'n' ? '\n' : c == 'r' ? '\r' : c); escaped = false; }
            else if (c == '\\') escaped = true; else out.append(c);
        }
        if (escaped) out.append('\\');
        return out.toString();
    }
}
