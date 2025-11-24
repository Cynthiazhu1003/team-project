package use_case2.entity;

import java.time.LocalDate;
import java.util.Objects;

public class Transaction {
//    private final String transactionId;
//    private final String accountId;
    private LocalDate date;
    private String description;
    private double amount;
    private String category;

//    private final String importedFrom;
//    private final String rawPayload;

    public Transaction(LocalDate date,
                       String description, double amount, String category) {
//        this.transactionId = transactionId;
//        this.accountId = accountId;
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.category = category;


    }

    // Getters
//    public String getTransactionId() { return transactionId; }
//    public String getAccountId() { return accountId; }
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }

//    public String getImportedFrom() { return importedFrom; }
//    public String getRawPayload() { return rawPayload; }

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
        return Double.compare(that.amount, amount) == 0 &&
                Objects.equals(description, that.description) &&
                Objects.equals(date, that.date) &&
                Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, description, amount, category);
    }
    @Override
    public String toString() {
        return "Transaction{" +
                ", description='" + description + '\'' +
                "date=" + date +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                '}';
    }
}