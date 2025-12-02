package use_case2.interface_adapter.add_transaction;


import use_case2.use_case_addTrans.AddTransactionInputBoundary;
import use_case2.use_case_addTrans.AddTransactionInputData;
import java.time.LocalDate;

public class AddTransactionController {
    private final AddTransactionInputBoundary addTransactionUseCase;

    public AddTransactionController(AddTransactionInputBoundary addTransactionUseCase) {
        this.addTransactionUseCase = addTransactionUseCase;
    }

    public void execute(LocalDate date, String description, String merchant, double amount, String category) {
        AddTransactionInputData inputData = new AddTransactionInputData(
                date, description, merchant, amount, category
        );
        addTransactionUseCase.execute(inputData);
    }
}