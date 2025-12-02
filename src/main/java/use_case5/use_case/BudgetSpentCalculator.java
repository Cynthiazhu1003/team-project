package use_case5.use_case;

import frontend.Transaction;
import use_case2.use_case.TransactionDataAccessInterface;

import java.time.LocalDate;
import java.util.List;

// Helper class for Budget use cases
public class BudgetSpentCalculator {

    private final TransactionDataAccessInterface transactionDAO;

    public BudgetSpentCalculator(TransactionDataAccessInterface dao) {
        this.transactionDAO = dao;
    }

    public double calculateSpent(String category) {
        List<Transaction> all = transactionDAO.getAllTransactions();
        LocalDate firstDay = LocalDate.now().withDayOfMonth(1);

        return all.stream()
                .filter(t -> t.getCategory() != null && t.getCategory().equals(category))
                .filter(t -> !t.getDate().isBefore(firstDay))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
}