package use_case4.interface_adapter.viewmodel;

import frontend.Transaction;
import java.util.List;

public class AutoCategorizeViewModel {

    private List<Transaction> categorizedTransactions;
    private String error;

    public List<Transaction> getCategorizedTransactions() {
        return categorizedTransactions;
    }

    public void setCategorizedTransactions(List<Transaction> categorizedTransactions) {
        this.categorizedTransactions = categorizedTransactions;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}