package use_case5.use_case;

import use_case5.boundary.*;
import use_case5.data.BudgetRepository;
import frontend.Budget;

public class RefreshBudgetsInteractor implements RefreshBudgetsInputBoundary {

    private final BudgetRepository repository;
    private final BudgetOutputBoundary presenter;
    private final BudgetSpentCalculator spentCalculator;

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