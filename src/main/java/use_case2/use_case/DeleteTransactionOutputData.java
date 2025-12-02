package use_case2.use_case;

import frontend.Transaction;
import java.util.List;

public class DeleteTransactionOutputData {
    private final String message;
    private final List<Transaction> updatedList;

    public DeleteTransactionOutputData(String message, List<Transaction> updatedList) {
        this.message = message;
        this.updatedList = updatedList;
    }

    public String getMessage() { return message; }
    public List<Transaction> getUpdatedList() { return updatedList; }
}
