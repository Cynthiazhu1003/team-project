package use_case2.use_case_deleteTrans;

/**
 * The input boundary for the Delete Transaction use case.
 * Defines the method the Controller calls to initiate deletion.
 */
public interface DeleteTransactionInputBoundary {
    /**
     * Executes the deletion of a transaction.
     * @param index The index (or conceptual ID) of the transaction to delete.
     */
    void execute(int index);
}
