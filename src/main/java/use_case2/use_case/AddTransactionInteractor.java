package use_case2.use_case;


import frontend.Transaction;

import java.util.List;

public class AddTransactionInteractor implements AddTransactionInputBoundary {
    private final AddTransactionOutputBoundary presenter;
    private final TransactionDataAccessInterface transactionDataAccess;

    public AddTransactionInteractor(AddTransactionOutputBoundary presenter,
                                    TransactionDataAccessInterface transactionDataAccess) {
        this.presenter = presenter;
        this.transactionDataAccess = transactionDataAccess;
    }

    @Override
    public void execute(AddTransactionInputData inputData) {
        try {
            if (inputData.getDescription() == null || inputData.getDescription().trim().isEmpty()) {
                presenter.prepareFailView("Please fill in the Store Name");
                return;
            }
            if (inputData.getMerchant() == null || inputData.getMerchant().trim().isEmpty()) {
                presenter.prepareFailView("Merchant is required");
                return;
            }

            if (inputData.getAmount() == 0) {
                presenter.prepareFailView("Please enter a non-zero Amount.");
                return;
            }
            Transaction transaction = new Transaction(
                    inputData.getDate(),
                    inputData.getDescription(),
                    inputData.getMerchant(),
                    inputData.getAmount(),
                    inputData.getCategory()
            );
            transactionDataAccess.save(transaction);
            List<Transaction> updatedList = transactionDataAccess.getAllTransactions();
            AddTransactionOutputData outputData = new AddTransactionOutputData(true, null, updatedList);
            presenter.prepareSuccessView(outputData);
        } catch (Exception e) {
            presenter.prepareFailView("Failed to add transaction: " + e.getMessage());
        }
    }
}

