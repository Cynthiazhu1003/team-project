package use_case2.use_case;

import use_case2.entity.Transaction;

public interface TransactionDataAccessInterface {
    void save(Transaction transaction);
}
