package use_case2.interface_adapter.delete_transaction;

import use_case2.use_case.DeleteTransactionInputBoundary;

/**
 * The Controller for the Delete Transaction use case.
 * It is called by the GUI's button action handler.
 */
public class DeleteTransactionController {
    private final DeleteTransactionInputBoundary deleteTransactionUseCase;

    public DeleteTransactionController(DeleteTransactionInputBoundary deleteTransactionUseCase) {
        this.deleteTransactionUseCase = deleteTransactionUseCase;
    }

    /**
     * Triggers the deletion use case.
     * @param index The index (row index) of the transaction to delete.
     */
    public void execute(int index) {
        deleteTransactionUseCase.execute(index);
    }
}
