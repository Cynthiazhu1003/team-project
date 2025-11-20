package use_case2.use_case;

import java.time.LocalDate;

/**
 * Input data for updating budget spending when a transaction is added.
 */
public class UpdateBudgetSpendingInputData {
    private final String category;
    private final double amount;
    private final LocalDate transactionDate;

    public UpdateBudgetSpendingInputData(String category, double amount, LocalDate transactionDate) {
        this.category = category;
        this.amount = amount;
        this.transactionDate = transactionDate;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }
}
