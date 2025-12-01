package use_case5.interface_adapter;

import use_case5.boundary.*;

public class BudgetPresenter implements BudgetOutputBoundary {
    private final BudgetViewModel viewModel;

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