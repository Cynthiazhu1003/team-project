package use_case2.interface_adapter.add_budget;

import use_case2.use_case.AddBudgetOutputBoundary;
import use_case2.use_case.AddBudgetOutputData;

/**
 * Presenter for adding budgets.
 * Receives output from interactor and updates the view state.
 */
public class AddBudgetPresenter implements AddBudgetOutputBoundary {
    private final BudgetState budgetState;

    /**
     * Creates an AddBudgetPresenter with the budget state.
     *
     * @param budgetState The state to update
     */
    public AddBudgetPresenter(BudgetState budgetState) {
        this.budgetState = budgetState;
    }

    @Override
    public void prepareSuccessView(AddBudgetOutputData outputData) {
        budgetState.clearMessages();
        budgetState.setBudgetSuccess(
            String.format("Budget set for %s: $%.2f",
                outputData.getCategory(),
                outputData.getMonthlyLimit())
        );
    }

    @Override
    public void prepareFailView(String errorMessage) {
        budgetState.clearMessages();
        budgetState.setBudgetError(errorMessage);
    }
}
