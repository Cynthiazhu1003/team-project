package use_case2.interface_adapter.delete_transaction;

import use_case2.interface_adapter.ViewManagerModel;
import use_case2.interface_adapter.transaction.TransactionState;
import use_case2.interface_adapter.transaction.TransactionViewModel;
import use_case2.use_case.DeleteTransactionOutputBoundary;

public class DeleteTransactionPresenter implements DeleteTransactionOutputBoundary {
    private final TransactionViewModel transactionViewModel;
    private final ViewManagerModel viewManagerModel;

    public DeleteTransactionPresenter(ViewManagerModel viewManagerModel,
                                      TransactionViewModel transactionViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.transactionViewModel = transactionViewModel;
    }

    @Override
    public void prepareSuccessView(String successMessage) {
        TransactionState currentState = transactionViewModel.getState();
        currentState.setTransactionError(null);
        currentState.setTransactionSuccess(successMessage);

        transactionViewModel.setState(currentState);
        transactionViewModel.firePropertyChanged();

        // No view change needed, just an update to the current transaction view
    }

    @Override
    public void prepareFailView(String errorMessage) {
        TransactionState currentState = transactionViewModel.getState();
        currentState.setTransactionError(errorMessage);
        currentState.setTransactionSuccess(null);

        transactionViewModel.setState(currentState);
        transactionViewModel.firePropertyChanged();
    }
}