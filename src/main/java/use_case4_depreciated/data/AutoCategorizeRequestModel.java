package use_case4_depreciated.data;

import frontend.Transaction;
import java.util.List;

public class AutoCategorizeRequestModel {

    private final List<Transaction> transactions;

    public AutoCategorizeRequestModel(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}