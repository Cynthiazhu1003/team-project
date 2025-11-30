package use_case2.use_case;

import use_case2.data_access.InMemoryTransactionDataAccessObject;

public class DeleteTransactionInteractor implements DeleteTransactionInputBoundary {

    private final InMemoryTransactionDataAccessObject dataAccessObject;
    private final DeleteTransactionOutputBoundary transactionPresenter;

    public DeleteTransactionInteractor(InMemoryTransactionDataAccessObject dataAccessObject,
                                       DeleteTransactionOutputBoundary transactionPresenter) {
        this.dataAccessObject = dataAccessObject;
        this.transactionPresenter = transactionPresenter;
    }

    @Override
    public void execute(int index) {
        boolean success = dataAccessObject.deleteByIndex(index);

        if (success) {
            transactionPresenter.prepareSuccessView("Transaction deleted successfully!");
        } else {
            transactionPresenter.prepareFailView("Error: Could not find transaction at index " + index);
        }
    }
}