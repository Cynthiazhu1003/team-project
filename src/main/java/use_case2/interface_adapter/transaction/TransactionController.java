package use_case2.interface_adapter.transaction;

import frontend.Transaction;
import use_case2.data_access.InMemoryTransactionDataAccessObject;
import java.util.List;

public class TransactionController {

    private final InMemoryTransactionDataAccessObject dao;

    public TransactionController(InMemoryTransactionDataAccessObject dao) {
        this.dao = dao;
    }

    public void addTransaction(Transaction t) {
        dao.save(t);
    }

    public void updateTransaction(Transaction oldTransaction, Transaction newTransaction) {
        dao.update(oldTransaction);
    }

    public void deleteTransaction(Transaction t) {
        dao.delete(t);
    }

    public List<Transaction> getAllTransactions() {
        return dao.getAllTransactions();
    }
}