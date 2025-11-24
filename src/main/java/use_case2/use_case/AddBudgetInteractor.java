package use_case2.use_case;

import use_case2.entity.Budget;
import java.time.YearMonth;

/**
 * Interactor for adding a budget.
 * Contains the core business logic for creating and validating budgets.
 */
public class AddBudgetInteractor implements AddBudgetInputBoundary {
    private final AddBudgetOutputBoundary presenter;
    private final BudgetDataAccessInterface budgetDataAccess;

    /**
     * Creates an AddBudgetInteractor with dependencies.
     *
     * @param presenter The output boundary for presenting results
     * @param budgetDataAccess The data access interface for persisting budgets
     */
    public AddBudgetInteractor(AddBudgetOutputBoundary presenter,
                              BudgetDataAccessInterface budgetDataAccess) {
        this.presenter = presenter;
        this.budgetDataAccess = budgetDataAccess;
    }

    @Override
    public void execute(AddBudgetInputData inputData) {
        // Validation: Category cannot be null or empty
        if (inputData.getCategory() == null || inputData.getCategory().trim().isEmpty()) {
            presenter.prepareFailView("Category is required");
            return;
        }

        // Validation: Monthly limit must be positive
        if (inputData.getMonthlyLimit() <= 0) {
            presenter.prepareFailView("Monthly limit must be greater than 0");
            return;
        }

        // Validation: Month cannot be null
        if (inputData.getMonth() == null) {
            presenter.prepareFailView("Month is required");
            return;
        }

        String category = inputData.getCategory().trim();
        YearMonth month = inputData.getMonth();

        // Check if budget already exists for this category and month
        if (budgetDataAccess.exists(category, month)) {
            // Update existing budget
            budgetDataAccess.getByCategoryAndMonth(category, month).ifPresent(existingBudget -> {
                existingBudget.setMonthlyLimit(inputData.getMonthlyLimit());
                budgetDataAccess.save(existingBudget);
            });
        } else {
            // Create new budget entity
            Budget budget = new Budget(category, inputData.getMonthlyLimit(), month);

            // Persist the budget
            budgetDataAccess.save(budget);
        }

        // Prepare success response
        AddBudgetOutputData outputData = new AddBudgetOutputData(
            true,
            null,
            category,
            inputData.getMonthlyLimit()
        );
        presenter.prepareSuccessView(outputData);
    }
}
