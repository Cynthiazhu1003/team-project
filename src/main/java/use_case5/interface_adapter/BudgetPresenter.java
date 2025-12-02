package use_case5.interface_adapter;

import use_case5.boundary.BudgetNotificationModel;
import use_case5.boundary.BudgetOutputBoundary;
import use_case5.boundary.BudgetResponseModel;

/**
 * Budget presenter for the Budget use cases.
 */
public class BudgetPresenter implements BudgetOutputBoundary {
    private final BudgetViewModel viewModel;

    /**
     * Constructor for the Budget Presenter.
     */
    public BudgetPresenter(BudgetViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentBudget(BudgetResponseModel response) {
        // Just update the data
        viewModel.setBudget(response);
    }

    @Override
    public void presentNotification(BudgetNotificationModel notification) {
        viewModel.setNotification(notification);
    }
}