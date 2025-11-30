package use_case2.data_access;

import frontend.Transaction;
import use_case2.use_case.TransactionDataAccessInterface;
import java.util.ArrayList;
import java.util.List;

public class InMemoryTransactionDataAccessObject implements TransactionDataAccessInterface {
    private final List<Transaction> transactions = new ArrayList<>();

    @Override
    public void save(Transaction transaction) {
        transactions.add(transaction);
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    @Override
    public void update(Transaction transaction) {
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).equals(transaction)) {
                transactions.set(i, transaction);
                return;
            }
        }
    }
    /**
     * Deletes a transaction from the list based on its index.
     * @param index The zero-based index of the transaction to delete.
     * @return true if deletion was successful, false otherwise.
     */
    public boolean deleteByIndex(int index) { // ⭐️ MUST be public boolean and take an int ⭐️
        if (index >= 0 && index < transactions.size()) {
            transactions.remove(index);
            return true;
        }
        return false;
    }
    @Override
    public void delete(Transaction transaction) {transactions.remove(transaction);}

    public int getTransactionCount() {
        return transactions.size();
    }
}
