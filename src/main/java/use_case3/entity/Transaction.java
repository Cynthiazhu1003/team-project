package use_case3.entity;

import java.time.LocalDate;
import java.util.Objects;

public class Transaction {

    private LocalDate date;
    private String description;
    private String merchant;
    private double amount;
    private String category;

    public Transaction(LocalDate date,
                       String description,
                       String merchant,
                       double amount,
                       String category) {
        this.date = date;
        this.description = description;
        this.merchant = merchant;
        this.amount = amount;
        this.category = category;
    }

    // ----------- Getters -----------

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getMerchant() {
        return merchant;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    // ----------- Setters -----------

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    // ----------- equals & hashCode -----------

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

    // ----------- toString -----------

    @Override
    public String toString() {
        return "Transaction{" +
                "date=" + date +
                ", description='" + description + '\'' +
                ", merchant='" + merchant + '\'' +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                '}';
    }
}