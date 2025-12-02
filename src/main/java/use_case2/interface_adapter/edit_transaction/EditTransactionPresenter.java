package use_case2.interface_adapter.edit_transaction;

import use_case2.use_case_editTrans.EditTransactionOutputBoundary;
import use_case2.use_case_editTrans.EditTransactionOutputData;
import use_case2.interface_adapter.transaction_Managment.ViewManagerModel;
import use_case2.interface_adapter.transaction_Managment.TransactionState;
import use_case2.interface_adapter.transaction_Managment.TransactionViewModel;

public class EditTransactionPresenter implements EditTransactionOutputBoundary {
    private final TransactionViewModel transactionViewModel;
    private final ViewManagerModel viewManagerModel;

    public EditTransactionPresenter(ViewManagerModel viewManagerModel,
                                    TransactionViewModel transactionViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.transactionViewModel = transactionViewModel;
    }

    @Override
    public void prepareSuccessView(EditTransactionOutputData outputData) {
        TransactionState currentState = transactionViewModel.getState();
        currentState.setEditingTransactionIndex(-1);
        currentState.setDate(null);
        currentState.setDescription("");
        currentState.setMerchant("");
        currentState.setAmount(0.0);
        currentState.setCategory("");
        currentState.setTransactionError(null);
        currentState.setTransactionSuccess("Transaction updated successfully!");

        transactionViewModel.setState(currentState);
        transactionViewModel.firePropertyChanged();
        viewManagerModel.setActiveView(transactionViewModel.getViewName());
        viewManagerModel.firePropertyChanged();
    }

    @Override
    public void prepareFailView(String error) {
        TransactionState currentState = transactionViewModel.getState();
        currentState.setTransactionError(error);
        currentState.setTransactionSuccess(null);
        transactionViewModel.setState(currentState);
        transactionViewModel.firePropertyChanged();
    }
}