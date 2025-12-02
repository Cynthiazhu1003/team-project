package use_case5.use_case;

import frontend.Transaction;
import java.time.LocalDate;
import java.util.List;
import use_case2.data_access.TransactionDataAccessInterface;

/**
 * Helper class to calculate Budget spent for each category.
 */
public class BudgetSpentCalculator {
    private final TransactionDataAccessInterface transactionDao;

    /**
     * Constructor for the calculator.
     */
    public BudgetSpentCalculator(TransactionDataAccessInterface dao) {
        this.transactionDao = dao;
    }

    /**
     * Actual method to calculate the spending of a category.
     */
    public double calculateSpent(String category) {
        List<Transaction> all = transactionDao.getAllTransactions();
        LocalDate firstDay = LocalDate.now().withDayOfMonth(1);

        return all.stream()
                .filter(t -> t.getCategory() != null && t.getCategory().equals(category))
                .filter(t -> !t.getDate().isBefore(firstDay))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
}