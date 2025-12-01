package frontend;

import java.time.LocalDate;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;

public class Transaction {

    private LocalDate date;
    private String description;
    private String merchant;
    private double amount;
    private String category;

    private static List<Transaction> allTransactions = new ArrayList<>();

    public static void addAll(List<Transaction> transactions) {
        allTransactions.addAll(transactions);
    }

    public static List<Transaction> getAll() {
        return allTransactions;
    }

    // Full constructor including category
    public Transaction(LocalDate date, String description, String merchant, double amount, String category) {
        this.date = date;
        this.description = description;
        this.merchant = merchant;
        this.amount = amount;
        this.category = category;
    }

    // Constructor without category
    public Transaction(LocalDate date, String description, String merchant, double amount) {
        this(date, description, merchant, amount, null);
    }

    // Static factory to parse from CSV line
    public static Transaction of(String line) {
        String[] parts = line.split(",");
        LocalDate date = LocalDate.parse(parts[0]);
        String description = parts[1];
        String merchant = parts[2];
        double amount = Double.parseDouble(parts[3]);
        return new Transaction(date, description, merchant, amount);
    }

    // Getters
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
    public String getMerchant() { return merchant; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }

    // Setters
    public void setDate(LocalDate date) { this.date = date; }
    public void setDescription(String description) { this.description = description; }
    public void setMerchant(String merchant) { this.merchant = merchant; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setCategory(String category) { this.category = category; }

    // Equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Double.compare(that.amount, amount) == 0 &&
                Objects.equals(date, that.date) &&
                Objects.equals(description, that.description) &&
                Objects.equals(merchant, that.merchant) &&
                Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, description, merchant, amount, category);
    }

    // toString
    @Override
    public String toString() {
        return String.format("Transaction{date=%s, description='%s', merchant='%s', amount=%.2f, category='%s'}",
                date, description, merchant, amount, category);
    }
}