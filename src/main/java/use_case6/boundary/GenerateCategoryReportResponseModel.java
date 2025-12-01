package use_case6.boundary;

import java.time.LocalDate;
import java.util.List;

public class GenerateCategoryReportResponseModel {

    public static class TransactionSummary {
        private final LocalDate date;
        private final String description;
        private final double amount;

        public TransactionSummary(LocalDate date, String description, double amount) {
            this.date = date;
            this.description = description;
            this.amount = amount;
        }

        public LocalDate getDate() { return date; }
        public String getDescription() { return description; }
        public double getAmount() { return amount; }
    }

    private final String category;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final double totalAmount;
    private final int transactionCount;
    private final List<TransactionSummary> transactions;

    public GenerateCategoryReportResponseModel(
            String category,
            LocalDate startDate,
            LocalDate endDate,
            double totalAmount,
            int transactionCount,
            List<TransactionSummary> transactions) {

        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalAmount = totalAmount;
        this.transactionCount = transactionCount;
        this.transactions = transactions;
    }

    public String getCategory() { return category; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public double getTotalAmount() { return totalAmount; }
    public int getTransactionCount() { return transactionCount; }
    public List<TransactionSummary> getTransactions() { return transactions; }
}