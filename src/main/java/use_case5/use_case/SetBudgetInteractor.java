package use_case5.use_case;

import use_case5.boundary.*;
import use_case5.data.BudgetRepository;
import frontend.Budget;

public class SetBudgetInteractor implements SetBudgetInputBoundary {

    private final BudgetRepository repository;
    private final BudgetOutputBoundary presenter;
    private final BudgetSpentCalculator spentCalculator;

    public SetBudgetInteractor(BudgetRepository repo,
                               BudgetSpentCalculator spentCalc,
                               BudgetOutputBoundary presenter) {
        this.repository = repo;
        this.spentCalculator = spentCalc;
        this.presenter = presenter;
    }

    @Override
    public void setBudget(SetBudgetRequestModel request) {
        Budget budget = new Budget(request.category, request.limit, request.month);

        double spent = spentCalculator.calculateSpent(request.category);
        budget.setSpent(spent);

        repository.save(budget);

        presenter.presentBudget(BudgetResponseModel.from(budget));
        presenter.presentNotification(BudgetNotificationModel.buildWarning(budget, false));
    }
}