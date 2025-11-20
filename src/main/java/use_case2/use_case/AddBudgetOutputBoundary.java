package use_case2.use_case;

/**
 * Output boundary interface for adding a budget.
 * Called by interactor to return results to presenter.
 */
public interface AddBudgetOutputBoundary {
    /**
     * Prepares the view for a successful budget addition.
     *
     * @param outputData The output data containing success information
     */
    void prepareSuccessView(AddBudgetOutputData outputData);

    /**
     * Prepares the view for a failed budget addition.
     *
     * @param errorMessage The error message to display
     */
    void prepareFailView(String errorMessage);
}
