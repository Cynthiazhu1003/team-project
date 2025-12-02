package use_case5.use_case;

import frontend.Budget;
import use_case5.boundary.BudgetNotificationModel;
import use_case5.boundary.BudgetOutputBoundary;
import use_case5.boundary.BudgetResponseModel;
import use_case5.boundary.RefreshBudgetsInputBoundary;
import use_case5.data.BudgetRepository;

/**
 * Interactor for the refresh budget use case, implements the corresponding boundary interface.
 */
public class RefreshBudgetsInteractor implements RefreshBudgetsInputBoundary {
    private final BudgetRepository repository;
    private final BudgetOutputBoundary presenter;
    private final BudgetSpentCalculator spentCalculator;

    /**
     * Constructor for refresh budget interactor.
     */
    public RefreshBudgetsInteractor(BudgetRepository repo,
                                    BudgetSpentCalculator spentCalc,
                                    BudgetOutputBoundary presenter) {
        this.repository = repo;
        this.spentCalculator = spentCalc;
        this.presenter = presenter;
    }

    @Override
    public void refreshAllBudgets() {
        for (Budget budget : repository.findAll()) {

            double spent = spentCalculator.calculateSpent(budget.getCategory());
            budget.setSpent(spent);

            presenter.presentBudget(BudgetResponseModel.from(budget));
            presenter.presentNotification(
                    BudgetNotificationModel.buildWarning(budget, true)
            );
        }
    }
}