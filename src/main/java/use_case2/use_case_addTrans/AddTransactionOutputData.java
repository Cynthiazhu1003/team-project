package use_case2.use_case_addTrans;

import frontend.Transaction;

import java.util.List;

public class AddTransactionOutputData {
    private final boolean success;
    private final String errorMessage;
    private final List<Transaction> updatedTransactionList;

    public AddTransactionOutputData(boolean success, String errorMessage, List<Transaction> updatedList) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.updatedTransactionList = updatedList;
    }
    public List<Transaction> getUpdatedTransactionList() {
        return updatedTransactionList;
    }

    public boolean isSuccess() { return success; }
    public String getErrorMessage() { return errorMessage; }
}
