package use_case5.use_case;

import use_case5.boundary.*;
import use_case5.data.BudgetRepository;
import frontend.Budget;

public class DeleteBudgetInteractor implements DeleteBudgetInputBoundary {

    private final BudgetRepository repository;
    private final BudgetOutputBoundary presenter;

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