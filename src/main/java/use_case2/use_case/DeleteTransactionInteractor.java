package use_case2.use_case;

public class DeleteTransactionInteractor implements DeleteTransactionInputBoundary {

    private final DeleteTransactionOutputBoundary transactionPresenter;
    private final TransactionDataAccessInterface transactionDataAccess;

    public DeleteTransactionInteractor(DeleteTransactionOutputBoundary transactionPresenter,
                                       TransactionDataAccessInterface transactionDataAccess) {
        this.transactionDataAccess = transactionDataAccess;
        this.transactionPresenter = transactionPresenter;
    }

    @Override
    public void execute(int index) {
        boolean success = transactionDataAccess.deleteByIndex(index);

        if (success) {
            transactionPresenter.prepareSuccessView("Transaction deleted successfully!");
        } else {
            transactionPresenter.prepareFailView("Error: Could not find transaction at index " + index);
        }
    }
}