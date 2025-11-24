package use_case2.interface_adapter.add_budget;

import use_case2.use_case.AddBudgetInputBoundary;
import use_case2.use_case.AddBudgetInputData;
import java.time.YearMonth;

/**
 * Controller for adding budgets.
 * Receives raw input from UI and converts to input data DTO.
 */
public class AddBudgetController {
    private final AddBudgetInputBoundary addBudgetUseCase;

    /**
     * Creates an AddBudgetController with the use case dependency.
     *
     * @param addBudgetUseCase The use case to execute
     */
    public AddBudgetController(AddBudgetInputBoundary addBudgetUseCase) {
        this.addBudgetUseCase = addBudgetUseCase;
    }

    /**
     * Executes the add budget use case with the provided parameters.
     *
     * @param category The spending category
     * @param monthlyLimit The maximum amount allowed per month
     * @param month The year-month this budget applies to
     */
    public void execute(String category, double monthlyLimit, YearMonth month) {
        AddBudgetInputData inputData = new AddBudgetInputData(
            category,
            monthlyLimit,
            month
        );
        addBudgetUseCase.execute(inputData);
    }
}
