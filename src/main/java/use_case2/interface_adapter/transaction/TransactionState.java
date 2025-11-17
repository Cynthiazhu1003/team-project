package use_case2.interface_adapter.transaction;

import java.time.LocalDate;

public class TransactionState {
    private LocalDate date;
    private String category = "";
    private double amount = 0.0;
    private String description = "";
    private String transactionError;
    private String transactionSuccess;

    public TransactionState() {}
    public TransactionState(TransactionState copy) {
        this.date = copy.date;
        this.category = copy.category;
        this.amount = copy.amount;
        this.description = copy.description;
        this.transactionError = copy.transactionError;
        this.transactionSuccess = copy.transactionSuccess;
    }

    // Getters and Setters
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTransactionError() { return transactionError; }
    public void setTransactionError(String transactionError) { this.transactionError = transactionError; }
    public String getTransactionSuccess() { return transactionSuccess; }
    public void setTransactionSuccess(String transactionSuccess) { this.transactionSuccess = transactionSuccess; }
}
