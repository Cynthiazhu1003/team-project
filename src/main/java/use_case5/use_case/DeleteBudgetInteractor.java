package use_case5.use_case;

import frontend.Budget;
import use_case5.boundary.BudgetNotificationModel;
import use_case5.boundary.BudgetOutputBoundary;
import use_case5.boundary.BudgetResponseModel;
import use_case5.boundary.DeleteBudgetInputBoundary;
import use_case5.data.BudgetRepository;

/**
 * Interactor for the delete budget use case, implements the corresponding boundary interface.
 */
public class DeleteBudgetInteractor implements DeleteBudgetInputBoundary {
    private final BudgetRepository repository;
    private final BudgetOutputBoundary presenter;

    /**
     * Constructor for delete budget interactor.
     */
    public DeleteBudgetInteractor(BudgetRepository repo,
                                  BudgetOutputBoundary presenter) {
        this.repository = repo;
        this.presenter = presenter;
    }

    @Override
    public void deleteBudget(String category) {
        Budget budget = repository.find(category);

        if (budget == null) {
            throw new RuntimeException("Budget does not exist for category: " + category);
        }

        repository.delete(category);

        // Empty response to remove row from UI
        BudgetResponseModel deleted = BudgetResponseModel.deleted(category);
        presenter.presentBudget(deleted);

        presenter.presentNotification(
                new BudgetNotificationModel(
                        category,
                        "Budget for " + category + " has been deleted.",
                        BudgetNotificationModel.NotificationType.INFO
                )
        );
    }
}