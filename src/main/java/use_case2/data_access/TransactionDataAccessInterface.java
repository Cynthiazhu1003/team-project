package use_case2.data_access;

import frontend.Transaction;
import java.util.List;

public interface TransactionDataAccessInterface {

    void save(Transaction transaction);

    List<Transaction> getAllTransactions();

    void update(Transaction transaction);

    void delete(Transaction transaction);

    boolean deleteByIndex(int index);
}

