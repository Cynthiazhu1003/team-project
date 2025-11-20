package use_case2.use_case;

import use_case2.entity.Budget;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

/**
 * Data access interface for budget persistence.
 * Bridge between use case and data access layers.
 */
public interface BudgetDataAccessInterface {
    /**
     * Saves or updates a budget.
     *
     * @param budget The budget to save
     */
    void save(Budget budget);

    /**
     * Retrieves a budget by category and month.
     *
     * @param category The category to search for
     * @param month The month to search for
     * @return Optional containing the budget if found
     */
    Optional<Budget> getByCategoryAndMonth(String category, YearMonth month);

    /**
     * Retrieves all budgets for a specific month.
     *
     * @param month The month to retrieve budgets for
     * @return List of budgets for the specified month
     */
    List<Budget> getAllForMonth(YearMonth month);

    /**
     * Retrieves all budgets.
     *
     * @return List of all budgets
     */
    List<Budget> getAll();

    /**
     * Checks if a budget exists for a category and month.
     *
     * @param category The category to check
     * @param month The month to check
     * @return true if budget exists
     */
    boolean exists(String category, YearMonth month);

    /**
     * Deletes a budget by category and month.
     *
     * @param category The category to delete
     * @param month The month to delete
     */
    void delete(String category, YearMonth month);
}
