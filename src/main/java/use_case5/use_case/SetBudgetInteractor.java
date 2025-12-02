package use_case5.use_case;

import frontend.Budget;
import use_case5.boundary.BudgetNotificationModel;
import use_case5.boundary.BudgetOutputBoundary;
import use_case5.boundary.BudgetResponseModel;
import use_case5.boundary.SetBudgetInputBoundary;
import use_case5.boundary.SetBudgetRequestModel;
import use_case5.data.BudgetRepository;

/**
 * Interactor for the set budget use case, implements the corresponding boundary interface.
 */
public class SetBudgetInteractor implements SetBudgetInputBoundary {
    private final BudgetRepository repository;
    private final BudgetOutputBoundary presenter;
    private final BudgetSpentCalculator spentCalculator;

    /**
     * Constructor for set budget interactor.
     */
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