package use_case2.data_access;

import use_case2.entity.Budget;
import use_case2.use_case.BudgetDataAccessInterface;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory implementation of budget data access.
 * Stores budgets in memory using a HashMap.
 */
public class InMemoryBudgetDataAccessObject implements BudgetDataAccessInterface {
    // Map key: "category-YYYY-MM" -> Budget
    private final Map<String, Budget> budgets = new HashMap<>();

    /**
     * Generates a unique key for a budget based on category and month.
     *
     * @param category The category
     * @param month The month
     * @return Unique key string
     */
    private String generateKey(String category, YearMonth month) {
        return category + "-" + month.toString();
    }

    @Override
    public void save(Budget budget) {
        String key = generateKey(budget.getCategory(), budget.getMonth());
        budgets.put(key, budget);
    }

    @Override
    public Optional<Budget> getByCategoryAndMonth(String category, YearMonth month) {
        String key = generateKey(category, month);
        return Optional.ofNullable(budgets.get(key));
    }

    @Override
    public List<Budget> getAllForMonth(YearMonth month) {
        return budgets.values().stream()
                .filter(budget -> budget.getMonth().equals(month))
                .collect(Collectors.toList());
    }

    @Override
    public List<Budget> getAll() {
        return new ArrayList<>(budgets.values());
    }

    @Override
    public boolean exists(String category, YearMonth month) {
        String key = generateKey(category, month);
        return budgets.containsKey(key);
    }

    @Override
    public void delete(String category, YearMonth month) {
        String key = generateKey(category, month);
        budgets.remove(key);
    }
}
