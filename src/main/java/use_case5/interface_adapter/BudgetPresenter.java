package use_case5.interface_adapter;

import use_case5.boundary.*;
import use_case5.interface_adapter.BudgetViewModel;

public class BudgetPresenter implements BudgetOutputBoundary {

    private final BudgetViewModel viewModel;

    public BudgetPresenter(BudgetViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentBudget(BudgetResponseModel response) {
        viewModel.displayBudget(response);
    }

    @Override
    public void presentNotification(BudgetNotificationModel notification) {
        viewModel.showNotification(notification);
    }
}