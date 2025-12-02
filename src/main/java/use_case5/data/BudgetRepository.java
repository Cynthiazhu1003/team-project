package use_case5.data;

import frontend.Budget;
import java.util.Collection;

/**
 * Interface for the budget DAO.
 */
public interface BudgetRepository {
    /**
     * Finds the budget according to parameter category, returns the Budget.
     */
    Budget find(String category);

    /**
     * Saves the budget into the DAO.
     */
    void save(Budget budget);

    /**
     * Deletes the budget via category from the DAO.
     */
    void delete(String category);

    /**
     * Finds all Budgets in the DAO, returns a Collection of Budgets.
     */
    Collection<Budget> findAll();
}