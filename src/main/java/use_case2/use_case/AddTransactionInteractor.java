package use_case2.use_case;


import frontend.Transaction;

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
            // Since we removed merchant, we need different validation
            // Let's validate that we have at least a description or amount
            if (inputData.getDescription() == null || inputData.getDescription().trim().isEmpty()) {
                presenter.prepareFailView("Description is required");
                return;
            }
            if (inputData.getMerchant() == null || inputData.getMerchant().trim().isEmpty()) {
                presenter.prepareFailView("Merchant is required");
                return;
            }

            if (inputData.getAmount() == 0) {
                presenter.prepareFailView("Amount cannot be zero");
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
            AddTransactionOutputData outputData = new AddTransactionOutputData(true, null);
            presenter.prepareSuccessView(outputData);

        } catch (Exception e) {
            presenter.prepareFailView("Failed to add transaction: " + e.getMessage());
        }
    }
}

