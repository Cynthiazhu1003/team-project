package use_case2.use_case_deleteTrans;

import frontend.Transaction;
import use_case2.data_access.TransactionDataAccessInterface;

import java.util.List;

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
            List<Transaction> updatedList = transactionDataAccess.getAllTransactions();


            DeleteTransactionOutputData outputData =
                    new DeleteTransactionOutputData("Transaction deleted successfully!", updatedList);


            transactionPresenter.prepareSuccessView(outputData);
        } else {
            transactionPresenter.prepareFailView("Error: Could not find transaction at index " + index);
        }
    }
}