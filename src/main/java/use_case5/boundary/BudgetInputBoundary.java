package use_case5.boundary;

public interface BudgetInputBoundary {
    void setBudget(SetBudgetRequestModel requestModel);
    void addSpending(UpdateBudgetRequestModel requestModel);
}