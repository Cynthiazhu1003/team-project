package use_case2.use_case;

import use_case2.entity.Budget;
import java.time.YearMonth;
import java.util.Optional;

/**
 * Interactor for updating budget spending when transactions are added.
 * Automatically tracks spending against budgets.
 */
public class UpdateBudgetSpendingInteractor {
    private final BudgetDataAccessInterface budgetDataAccess;

    public UpdateBudgetSpendingInteractor(BudgetDataAccessInterface budgetDataAccess) {
        this.budgetDataAccess = budgetDataAccess;
    }

    /**
     * Updates the budget spending for a transaction.
     * If a budget exists for the transaction's category and month, adds the amount to spent.
     *
     * @param inputData The transaction information
     */
    public void execute(UpdateBudgetSpendingInputData inputData) {
        // Get the month from the transaction date
        YearMonth transactionMonth = YearMonth.from(inputData.getTransactionDate());

        // Find budget for this category and month
        Optional<Budget> budgetOpt = budgetDataAccess.getByCategoryAndMonth(
            inputData.getCategory(),
            transactionMonth
        );

        // If budget exists, update spending
        budgetOpt.ifPresent(budget -> {
            budget.addSpent(inputData.getAmount());
            budgetDataAccess.save(budget);
        });
    }
}
