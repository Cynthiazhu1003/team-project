package use_case5.interface_adapter;

import use_case5.boundary.*;

public class BudgetController {

    private final SetBudgetInputBoundary setBoundary;
    private final DeleteBudgetInputBoundary deleteBoundary;
    private final RefreshBudgetsInputBoundary refreshBoundary;

    public BudgetController(SetBudgetInputBoundary setBoundary,
                            DeleteBudgetInputBoundary deleteBoundary,
                            RefreshBudgetsInputBoundary refreshBoundary) {
        this.setBoundary = setBoundary;
        this.deleteBoundary = deleteBoundary;
        this.refreshBoundary = refreshBoundary;
    }

    public void setBudget(String category, double limit) {
        SetBudgetRequestModel req = new SetBudgetRequestModel();
        req.category = category;
        req.limit = limit;
        req.month = java.time.YearMonth.now();
        setBoundary.setBudget(req);
    }

    public void deleteBudget(String category){
        deleteBoundary.deleteBudget(category);
    }

    public void refreshAllBudgets() {
        refreshBoundary.refreshAllBudgets();
    }
}