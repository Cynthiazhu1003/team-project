package use_case2.use_case_edit_transactions;

import use_case2.entity.Transaction;
import use_case2.use_case.TransactionDataAccessInterface;

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
            // Validate input (same as Add Transaction)
            if (inputData.getDescription() == null || inputData.getDescription().trim().isEmpty()) {
                presenter.prepareFailView("Description (store name) is required");
                return;
            }

            if (inputData.getAmount() == 0) {
                presenter.prepareFailView("Amount cannot be zero");
                return;
            }

            // Get the transaction to edit
            var transactions = transactionDataAccess.getAllTransactions();
            if (inputData.getTransactionIndex() < 0 || inputData.getTransactionIndex() >= transactions.size()) {
                presenter.prepareFailView("Invalid transaction selected");
                return;
            }

            Transaction transactionToEdit = transactions.get(inputData.getTransactionIndex());

            // Update the transaction with new values
            transactionToEdit.setDate(inputData.getDate());
            transactionToEdit.setDescription(inputData.getDescription());
            transactionToEdit.setMerchant(inputData.getMerchant());
            transactionToEdit.setAmount(inputData.getAmount());
            transactionToEdit.setCategory(inputData.getCategory());

            // Save the updated transaction
            transactionDataAccess.update(transactionToEdit);

            // Prepare success view
            EditTransactionOutputData outputData = new EditTransactionOutputData(true, null);
            presenter.prepareSuccessView(outputData);

        } catch (Exception e) {
            presenter.prepareFailView("Failed to edit transaction: " + e.getMessage());
        }
    }
}