package use_case5.boundary;

/**
 * Delete budget input boundary for the delete budget use case.
 */
public interface DeleteBudgetInputBoundary {

    /**
     * Deletes a budget based on the parameter category.
     */
    void deleteBudget(String category);
}