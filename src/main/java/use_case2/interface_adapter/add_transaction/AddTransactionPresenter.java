package use_case2.interface_adapter.add_transaction;


import use_case2.use_case_addTrans.AddTransactionOutputBoundary;
import use_case2.use_case_addTrans.AddTransactionOutputData;
import use_case2.interface_adapter.transaction_Managment.ViewManagerModel;
import use_case2.interface_adapter.transaction_Managment.TransactionState;
import use_case2.interface_adapter.transaction_Managment.TransactionViewModel;

public class AddTransactionPresenter implements AddTransactionOutputBoundary {
    private final TransactionViewModel transactionViewModel;
    private final ViewManagerModel viewManagerModel;

    public AddTransactionPresenter(ViewManagerModel viewManagerModel,
                                   TransactionViewModel transactionViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.transactionViewModel = transactionViewModel;
    }

    @Override
    public void prepareSuccessView(AddTransactionOutputData outputData) {
        TransactionState currentState = transactionViewModel.getState();
        currentState.setDate(null);
        currentState.setAmount(0.0);
        currentState.setMerchant("");
        currentState.setCategory("");
        currentState.setDescription("");
        currentState.setTransactionError(null);
        currentState.setTransactionSuccess("Transaction added successfully!");
        currentState.setTransactions(outputData.getUpdatedTransactionList());

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
