package use_case2.entity;

import java.time.LocalDate;
import java.util.Objects;

public class Transaction {

    private LocalDate date;
    private String description;
    private double amount;
    private String category;


    public Transaction(LocalDate date,
                       String description, double amount, String category) {
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.category = category;


    }

    // Getters
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }


    // Setters for editable fields
    public void setDate(LocalDate date) { this.date = date; }
    public void setDescription(String description) { this.description = description; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setCategory(String category) { this.category = category; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(description, that.description) &&
                Double.compare(that.amount, amount) == 0 &&
                Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, description, amount, category);
    }
    @Override
    public String toString() {
        return String.format("Transaction{date=%s, category='%s',amount=%.2f, description='%s'}",
                date, description, amount, category);
    }
}