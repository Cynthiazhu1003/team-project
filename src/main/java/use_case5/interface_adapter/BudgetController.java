package use_case5.interface_adapter;

import use_case5.boundary.*;

public class BudgetController {

    private final BudgetInputBoundary interactor;

    public BudgetController(BudgetInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void setBudget(String category, double limit) {
        SetBudgetRequestModel req = new SetBudgetRequestModel();
        req.category = category;
        req.limit = limit;
        req.month = java.time.YearMonth.now();
        interactor.setBudget(req);
    }

    public void addSpending(String category, double amount) {
        UpdateBudgetRequestModel req = new UpdateBudgetRequestModel();
        req.category = category;
        req.amount = amount;
        interactor.addSpending(req);
    }
}