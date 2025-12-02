package use_case5.boundary;

import frontend.Budget;
import org.jetbrains.annotations.NotNull;

public class BudgetResponseModel {
    public String category;
    public double limit;
    public double spent;
    public double remaining;
    public String warningLevel;

    public static BudgetResponseModel from(Budget budget) {
        BudgetResponseModel res = new BudgetResponseModel();
        res.category = budget.getCategory();
        res.limit = budget.getMonthlyLimit();
        res.spent = budget.getSpent();
        res.remaining = budget.getRemaining();
        res.warningLevel = budget.getWarningLevel();
        return res;
    }

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