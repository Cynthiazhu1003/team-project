package use_case2.use_case;

/**
 * Input boundary interface for adding a budget.
 * Called by controller to trigger the use case.
 */
public interface AddBudgetInputBoundary {
    /**
     * Executes the add budget use case.
     *
     * @param inputData The input data containing budget information
     */
    void execute(AddBudgetInputData inputData);
}
