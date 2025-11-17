package use_case2.use_case;

import use_case2.entity.Transaction;
import java.util.List;

public interface TransactionDataAccessInterface {
    void save(Transaction transaction);
    List<Transaction> getAllTransactions();
    void update(Transaction transaction);
    void delete(Transaction transaction);
}