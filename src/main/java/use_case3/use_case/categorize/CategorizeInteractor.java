package use_case3.use_case.categorize;

import use_case3.entity.Transaction;

import java.util.List;

public class CategorizeInteractor implements CategorizeInputBoundary {

    private final List<Transaction> transactions;
    private final CategorizeOutputBoundary presenter;

    public CategorizeInteractor(List<Transaction> transactions,
                                CategorizeOutputBoundary presenter) {
        this.transactions = transactions;
        this.presenter = presenter;
    }

    @Override
    public void execute(CategorizeInputData inputData) {
        int index = inputData.getTransactionIndex();

        // 1. Check that the index is valid
        if (index < 0 || index >= transactions.size()) {
            presenter.presentFailure("Invalid transaction index.");
            return;
        }

        // 2. Get the transaction and update its category
        Transaction tx = transactions.get(index);
        tx.setCategory(inputData.getNewCategory());

        // 3. Recalculate the total amount for this category
        double total = 0;
        for (Transaction t : transactions) {
            if (inputData.getNewCategory().equals(t.getCategory())) {
                total += t.getAmount();
            }
        }

        // 4. Prepare and send output to presenter
        CategorizeOutputData outputData =
                new CategorizeOutputData(inputData.getNewCategory(), total);

        presenter.presentSuccess(outputData);
    }
}