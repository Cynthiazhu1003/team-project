package entity;

import java.time.LocalDate;
import java.util.Objects;

public class Transaction {
//    private final String transactionId;
//    private final String accountId;
    private LocalDate date;
    private double amount;
    private String category;
    private String description;
//    private final String importedFrom;
//    private final String rawPayload;

    public Transaction(LocalDate date,
                       double amount, String category,
                       String description) {
//        this.transactionId = transactionId;
//        this.accountId = accountId;
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.description = description;

    }

    // Getters
//    public String getTransactionId() { return transactionId; }
//    public String getAccountId() { return accountId; }
    public LocalDate getDate() { return date; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
//    public String getImportedFrom() { return importedFrom; }
//    public String getRawPayload() { return rawPayload; }

    // Setters for editable fields
    public void setDate(LocalDate date) { this.date = date; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Double.compare(that.amount, amount) == 0 &&
                Objects.equals(date, that.date) &&
                Objects.equals(category, that.category) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, amount, category, description);
    }
    @Override
    public String toString() {
        return "Transaction{" +
                "date=" + date +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}