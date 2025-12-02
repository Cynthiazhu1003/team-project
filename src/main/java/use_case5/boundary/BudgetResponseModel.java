package use_case5.boundary;

import frontend.Budget;

/**
 * An input data model for a budget response, used by the budget use cases.
 */
public class BudgetResponseModel {
    public String category;
    public double limit;
    public double spent;
    public double remaining;
    public String warningLevel;

    /**
     * Formats a budget into a good budget response model.
     */
    public static BudgetResponseModel from(Budget budget) {
        BudgetResponseModel res = new BudgetResponseModel();
        res.category = budget.getCategory();
        res.limit = budget.getMonthlyLimit();
        res.spent = budget.getSpent();
        res.remaining = budget.getRemaining();
        res.warningLevel = budget.getWarningLevel();
        return res;
    }

    /**
     * Formats a budget into a deleted budget response model.
     */
    public static BudgetResponseModel deleted(String category) {
        BudgetResponseModel res = new BudgetResponseModel();
        res.category = category;
        res.limit = 0;
        res.spent = 0;
        res.remaining = 0;
        res.warningLevel = "DELETED";
        return res;
    }
}