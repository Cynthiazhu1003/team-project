package use_case5.use_case;

import use_case5.boundary.*;
import use_case5.data.BudgetRepository;
import frontend.Budget;
import frontend.Transaction;
import use_case2.use_case.TransactionDataAccessInterface;

import java.util.List;

/**
 * Interactor for handling budget operations.
 * Automatically recalculates spending using TransactionDAO.
 */
public class BudgetInteractor implements BudgetInputBoundary {

    private final BudgetRepository repository;
    private final BudgetOutputBoundary presenter;
    private final TransactionDataAccessInterface transactionDAO;

    public BudgetInteractor(BudgetRepository repository,
                            TransactionDataAccessInterface transactionDAO,
                            BudgetOutputBoundary presenter) {
        this.repository = repository;
        this.transactionDAO = transactionDAO;
        this.presenter = presenter;
    }

    /**
     * Sets a new budget for a category/month.
     * Initial spent value comes from existing transactions.
     */
    @Override
    public void setBudget(SetBudgetRequestModel request) {
        Budget budget = new Budget(request.category, request.limit, request.month);

        // Auto-calc spent from DAO
        double spent = calculateSpent(request.category);
        budget.setSpent(spent);

        repository.save(budget);
        presenter.presentBudget(buildResponse(budget));
    }

    /**
     * Recomputes total spending for this category.
     * Ignores the incoming amount because DAO is the source of truth.
     */
    @Override
    public void addSpending(UpdateBudgetRequestModel request) {
        Budget budget = repository.find(request.category);

        if (budget == null) {
            throw new RuntimeException("Budget does not exist for category: " + request.category);
        }

        // FULLY recalc based on all transactions (grouped by category)
        double spent = calculateSpent(request.category);
        budget.setSpent(spent);

        repository.save(budget);

        presenter.presentBudget(buildResponse(budget));

        // Trigger UI notification if needed
        if (!budget.getWarningLevel().equals("OK")) {
            BudgetNotificationModel notif = new BudgetNotificationModel();
            notif.category = budget.getCategory();
            notif.message = "Budget " + budget.getWarningLevel();
            presenter.presentNotification(notif);
        }
    }

    /**
     * Sums all transactions in DAO for this category.
     */
    private double calculateSpent(String category) {
        List<Transaction> all = transactionDAO.getAllTransactions();
        return all.stream()
                .filter(t -> t.getCategory().equals(category)) // grouped by category
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    /**
     * Converts Budget → ResponseModel
     */
    private BudgetResponseModel buildResponse(Budget budget) {
        BudgetResponseModel res = new BudgetResponseModel();
        res.category = budget.getCategory();
        res.limit = budget.getMonthlyLimit();
        res.spent = budget.getSpent();
        res.remaining = budget.getRemaining();
        res.warningLevel = budget.getWarningLevel();
        return res;
    }
}