package use_case5.use_case;

import use_case5.boundary.*;
import use_case5.data.BudgetRepository;
import frontend.Budget;

/**
 * Interactor for handling budget operations.
 * Implements the input boundary.
 */
public class BudgetInteractor implements BudgetInputBoundary {

    private final BudgetRepository repository;
    private final BudgetOutputBoundary presenter;

    public BudgetInteractor(BudgetRepository repository, BudgetOutputBoundary presenter) {
        this.repository = repository;
        this.presenter = presenter;
    }

    /**
     * Sets a new budget for a given category and month.
     */
    @Override
    public void setBudget(SetBudgetRequestModel request) {
        Budget budget = new Budget(request.category, request.limit, request.month);
        repository.save(budget);

        BudgetResponseModel response = buildResponse(budget);
        presenter.presentBudget(response);
    }

    /**
     * Updates spending for a budget category.
     * Can handle negative amounts for adjustments or edits.
     */
    @Override
    public void addSpending(UpdateBudgetRequestModel request) {
        Budget budget = repository.find(request.category);

        if (budget == null) {
            throw new RuntimeException("Budget does not exist for category: " + request.category);
        }

        // Add the amount (can be negative if subtracting)
        budget.addSpent(request.amount);

        // Save updated budget
        repository.save(budget);

        // Build and send response to presenter
        BudgetResponseModel response = buildResponse(budget);
        presenter.presentBudget(response);

        // Notify if over limit
        if (!budget.getWarningLevel().equals("OK")) {
            BudgetNotificationModel notif = new BudgetNotificationModel();
            notif.category = budget.getCategory();
            notif.message = "Budget " + budget.getWarningLevel();
            presenter.presentNotification(notif);
        }
    }

    /**
     * Converts a Budget object into a BudgetResponseModel for the presenter.
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