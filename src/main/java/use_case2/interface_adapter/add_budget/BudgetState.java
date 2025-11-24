package use_case2.interface_adapter.add_budget;

import java.time.YearMonth;

/**
 * State holder for budget UI.
 * Holds mutable UI state that persists across view updates.
 */
public class BudgetState {
    private String category = "";
    private double monthlyLimit = 0.0;
    private YearMonth month = YearMonth.now();
    private String budgetError;
    private String budgetSuccess;

    /**
     * Creates a new BudgetState with default values.
     */
    public BudgetState() {
    }

    /**
     * Copy constructor for state management.
     *
     * @param copy The state to copy
     */
    public BudgetState(BudgetState copy) {
        this.category = copy.category;
        this.monthlyLimit = copy.monthlyLimit;
        this.month = copy.month;
        this.budgetError = copy.budgetError;
        this.budgetSuccess = copy.budgetSuccess;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(double monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public YearMonth getMonth() {
        return month;
    }

    public void setMonth(YearMonth month) {
        this.month = month;
    }

    public String getBudgetError() {
        return budgetError;
    }

    public void setBudgetError(String budgetError) {
        this.budgetError = budgetError;
    }

    public String getBudgetSuccess() {
        return budgetSuccess;
    }

    public void setBudgetSuccess(String budgetSuccess) {
        this.budgetSuccess = budgetSuccess;
    }

    /**
     * Clears all messages (error and success).
     */
    public void clearMessages() {
        this.budgetError = null;
        this.budgetSuccess = null;
    }

    /**
     * Resets the form fields to default values.
     */
    public void resetForm() {
        this.category = "";
        this.monthlyLimit = 0.0;
        this.month = YearMonth.now();
        clearMessages();
    }
}
