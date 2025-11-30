package use_case4.data;

import frontend.Transaction;
import java.util.List;

public class AutoCategorizeResponseModel {

    private final List<Transaction> categorizedTransactions;

    public AutoCategorizeResponseModel(List<Transaction> categorizedTransactions) {
        this.categorizedTransactions = categorizedTransactions;
    }

    public List<Transaction> getCategorizedTransactions() {
        return categorizedTransactions;
    }
}