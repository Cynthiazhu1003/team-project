package use_case5.boundary;

/**
 * Set budget input boundary for the set budget use case.
 */
public interface SetBudgetInputBoundary {
    /**
     * Sets the budget via a set budget request.
     */
    void setBudget(SetBudgetRequestModel request);
}