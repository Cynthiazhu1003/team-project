package use_case2.use_case_editTrans;

import frontend.Transaction;
import use_case2.data_access.TransactionDataAccessInterface;

public class EditTransactionInteractor implements EditTransactionInputBoundary {
    private final EditTransactionOutputBoundary presenter;
    private final TransactionDataAccessInterface transactionDataAccess;

    public EditTransactionInteractor(EditTransactionOutputBoundary presenter,
                                     TransactionDataAccessInterface transactionDataAccess) {
        this.presenter = presenter;
        this.transactionDataAccess = transactionDataAccess;
    }
    @Override
    public void execute(EditTransactionInputData inputData) {
        try {
            if (inputData.getDescription() == null || inputData.getDescription().trim().isEmpty()) {
                presenter.prepareFailView("Please fill in the Store Name.");
                return;
            }
            if (inputData.getAmount() == 0) {
                presenter.prepareFailView("Please enter a non-zero Amount");
                return;
            }
            var transactions = transactionDataAccess.getAllTransactions();
            if (inputData.getTransactionIndex() < 0 || inputData.getTransactionIndex() >= transactions.size()) {
                presenter.prepareFailView("Invalid transaction selected");
                return;
            }
            Transaction transactionToEdit = transactions.get(inputData.getTransactionIndex());
            transactionToEdit.setDate(inputData.getDate());
            transactionToEdit.setDescription(inputData.getDescription());
            transactionToEdit.setMerchant(inputData.getMerchant());
            transactionToEdit.setAmount(inputData.getAmount());
            transactionToEdit.setCategory(inputData.getCategory());
            transactionDataAccess.update(transactionToEdit);
            EditTransactionOutputData outputData = new EditTransactionOutputData(true, null);
            presenter.prepareSuccessView(outputData);
        } catch (Exception e) {
            presenter.prepareFailView("Failed to edit transaction: " + e.getMessage());
        }
    }
}