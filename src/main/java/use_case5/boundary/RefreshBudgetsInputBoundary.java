package use_case5.boundary;

/**
 * Refresh budget input boundary for the refresh budget use case.
 */
public interface RefreshBudgetsInputBoundary {
    /**
     * Refreshes and recalculates all budgets and spending.
     */
    void refreshAllBudgets();
}