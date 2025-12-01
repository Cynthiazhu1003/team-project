package frontend;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Objects;

/**
 * Budget entity representing a monthly spending limit for a category.
 * Core business object with budget tracking logic.
 */
public class Budget {
    private final String category;
    private double monthlyLimit;
    private double spent;
    private final YearMonth month;
    private LocalDate lastUpdated;

    /**
     * Creates a new budget for a category with a monthly limit.
     *
     * @param category The spending category
     * @param monthlyLimit The maximum amount allowed per month
     * @param month The year-month this budget applies to
     */
    public Budget(String category, double monthlyLimit, YearMonth month) {
        this.category = category;
        this.monthlyLimit = monthlyLimit;
        this.spent = 0.0;
        this.month = month;
        this.lastUpdated = LocalDate.now();
    }

    public String getCategory() {
        return category;
    }

    public double getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(double monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
        this.lastUpdated = LocalDate.now();
    }

    public double getSpent() {
        return spent;
    }

    public void setSpent(double spent) {
        this.spent = spent;
        this.lastUpdated = LocalDate.now();
    }

    public void addSpent(double amount) {
        this.spent += amount;
        this.lastUpdated = LocalDate.now();
    }

    public YearMonth getMonth() {
        return month;
    }

    public LocalDate getLastUpdated() {
        return lastUpdated;
    }

    /**
     * Calculates the remaining budget amount.
     *
     * @return The amount remaining (positive) or overspent (negative)
     */
    public double getRemaining() {
        return monthlyLimit - spent;
    }

    /**
     * Calculates the percentage of budget used.
     *
     * @return Percentage of budget spent (0-100+)
     */
    public double getPercentageUsed() {
        if (monthlyLimit == 0) {
            return 0;
        }
        return (spent / monthlyLimit) * 100.0;
    }

    /**
     * Checks if spending is nearing the limit (>= 80%).
     *
     * @return true if spending is at or above 80% of limit
     */
    public boolean isNearingLimit() {
        return getPercentageUsed() >= 80.0;
    }

    /**
     * Checks if budget has been exceeded.
     *
     * @return true if spending exceeds the limit
     */
    public boolean isExceeded() {
        return spent > monthlyLimit;
    }

    /**
     * Gets the warning level for this budget.
     *
     * @return "EXCEEDED" if over limit, "WARNING" if >= 80%, "OK" otherwise
     */
    public String getWarningLevel() {
        if (isExceeded()) {
            return "EXCEEDED";
        } else if (isNearingLimit()) {
            return "WARNING";
        } else {
            return "OK";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Budget budget = (Budget) o;
        return Objects.equals(category, budget.category) &&
               Objects.equals(month, budget.month);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, month);
    }

    @Override
    public String toString() {
        return "Budget{" +
                "category='" + category + '\'' +
                ", monthlyLimit=" + monthlyLimit +
                ", spent=" + spent +
                ", month=" + month +
                ", remaining=" + getRemaining() +
                ", percentageUsed=" + String.format("%.1f%%", getPercentageUsed()) +
                ", warningLevel=" + getWarningLevel() +
                '}';
    }
}
