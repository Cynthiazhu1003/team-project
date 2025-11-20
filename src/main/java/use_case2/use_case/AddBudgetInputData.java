package use_case2.use_case;

import java.time.YearMonth;

/**
 * Input data transfer object for adding a budget.
 * Immutable DTO carrying input from controller to interactor.
 */
public class AddBudgetInputData {
    private final String category;
    private final double monthlyLimit;
    private final YearMonth month;

    /**
     * Creates input data for adding a budget.
     *
     * @param category The spending category
     * @param monthlyLimit The maximum amount allowed per month
     * @param month The year-month this budget applies to
     */
    public AddBudgetInputData(String category, double monthlyLimit, YearMonth month) {
        this.category = category;
        this.monthlyLimit = monthlyLimit;
        this.month = month;
    }

    public String getCategory() {
        return category;
    }

    public double getMonthlyLimit() {
        return monthlyLimit;
    }

    public YearMonth getMonth() {
        return month;
    }
}
