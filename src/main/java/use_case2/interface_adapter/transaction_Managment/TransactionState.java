package use_case2.interface_adapter.transaction_Managment;

import frontend.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionState {
    private LocalDate date;
    private String description = "";
    private String merchant = "";
    private double amount = 0.0;
    private String category = "";
    private String transactionError;
    private String transactionSuccess;
    private int editingTransactionIndex = -1;
    private List<Transaction> transactions;
    public TransactionState() {
        this.transactions = new ArrayList<>();
    }

    public TransactionState(TransactionState copy) {
        this.date = copy.date;
        this.category = copy.category;
        this.merchant = copy.merchant;
        this.amount = copy.amount;
        this.description = copy.description;
        this.transactionError = copy.transactionError;
        this.transactionSuccess = copy.transactionSuccess;
        this.editingTransactionIndex = copy.editingTransactionIndex;
        if (copy.transactions != null) {
            this.transactions = List.copyOf(copy.transactions);
        } else {
            this.transactions = null;
        }
    }

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTransactionError() {
        return transactionError;
    }

    public void setTransactionError(String transactionError) {
        this.transactionError = transactionError;
    }

    public String getTransactionSuccess() {
        return transactionSuccess;
    }

    public void setTransactionSuccess(String transactionSuccess) {
        this.transactionSuccess = transactionSuccess;
    }

    public int getEditingTransactionIndex() {
        return editingTransactionIndex;
    }

    public void setEditingTransactionIndex(int editingTransactionIndex) {
        this.editingTransactionIndex = editingTransactionIndex;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

}
