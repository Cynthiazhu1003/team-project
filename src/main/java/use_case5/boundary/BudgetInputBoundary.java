package use_case5.boundary;

public interface BudgetInputBoundary {
    void setBudget(SetBudgetRequestModel requestModel);
    void addSpending(UpdateBudgetRequestModel requestModel);

    double calculateSpent(String category);

    void deleteBudget(String category);

    void refreshAllBudgets();
}