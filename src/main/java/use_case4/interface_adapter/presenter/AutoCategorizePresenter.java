package use_case4.interface_adapter.presenter;

import use_case4.boundary.AutoCategorizeOutputBoundary;
import use_case4.data.AutoCategorizeResponseModel;
import use_case4.interface_adapter.viewmodel.AutoCategorizeViewModel;

public class AutoCategorizePresenter implements AutoCategorizeOutputBoundary {

    private final AutoCategorizeViewModel viewModel;

    public AutoCategorizePresenter(AutoCategorizeViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(AutoCategorizeResponseModel responseModel) {
        viewModel.setCategorizedTransactions(responseModel.getCategorizedTransactions());
        viewModel.setError(null);
    }

    @Override
    public void presentFailure(String error) {
        viewModel.setError(error);
    }
}