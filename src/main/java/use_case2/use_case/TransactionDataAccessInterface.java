package use_case2.use_case;

import frontend.Transaction;
import java.util.List;

public interface TransactionDataAccessInterface {

    void save(Transaction transaction);

    List<Transaction> getAllTransactions();

    void update(Transaction transaction);

    void delete(Transaction transaction);
}

