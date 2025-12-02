package use_case5.interface_adapter;

import use_case5.boundary.DeleteBudgetInputBoundary;
import use_case5.boundary.RefreshBudgetsInputBoundary;
import use_case5.boundary.SetBudgetInputBoundary;
import use_case5.boundary.SetBudgetRequestModel;

/**
 * Budget Controller for the Budget use cases.
 */
public class BudgetController {
    private final SetBudgetInputBoundary setBoundary;
    private final DeleteBudgetInputBoundary deleteBoundary;
    private final RefreshBudgetsInputBoundary refreshBoundary;

    /**
     * Constructor for the Budget Controller.
     */
    public BudgetController(SetBudgetInputBoundary setBoundary,
                            DeleteBudgetInputBoundary deleteBoundary,
                            RefreshBudgetsInputBoundary refreshBoundary) {
        this.setBoundary = setBoundary;
        this.deleteBoundary = deleteBoundary;
        this.refreshBoundary = refreshBoundary;
    }

    /**
     * Sends a set budget request to the set budget input boundary.
     */
    public void setBudget(String category, double limit) {
        SetBudgetRequestModel req = new SetBudgetRequestModel();
        req.category = category;
        req.limit = limit;
        req.month = java.time.YearMonth.now();
        setBoundary.setBudget(req);
    }

    /**
     * Sends a delete budget request to the delete budget input boundary.
     */
    public void deleteBudget(String category) {
        deleteBoundary.deleteBudget(category);
    }

    /**
     * Sends a refresh all budgets request to the refresh budgets input boundary.
     */
    public void refreshAllBudgets() {
        refreshBoundary.refreshAllBudgets();
    }
}