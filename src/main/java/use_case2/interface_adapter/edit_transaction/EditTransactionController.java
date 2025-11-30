package use_case2.interface_adapter.edit_transaction;

import use_case2.use_case_edit_transactions.EditTransactionInputBoundary;
import use_case2.use_case_edit_transactions.EditTransactionInputData;
import java.time.LocalDate;

public class EditTransactionController {
    private final EditTransactionInputBoundary editTransactionUseCase;

    public EditTransactionController(EditTransactionInputBoundary editTransactionUseCase) {
        this.editTransactionUseCase = editTransactionUseCase;
    }

    public void execute(int transactionIndex, LocalDate date, String description,
                        String merchant, double amount, String category) {
        EditTransactionInputData inputData = new EditTransactionInputData(
                transactionIndex, date, description, merchant, amount, category
        );
        editTransactionUseCase.execute(inputData);
    }
}
