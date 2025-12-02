package use_case5.data;

import frontend.Budget;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InMemoryBudgetRepository implements BudgetRepository {

    private final Map<String, Budget> storage = new HashMap<>();

    @Override
    public Budget find(String category) {
        Budget budget = storage.get(category);
        if (budget != null) {
            System.out.println("[DAO] Found budget for category '" + category +
                    "': Limit=" + budget.getMonthlyLimit() + ", Spent=" + budget.getSpent());
        } else {
            System.out.println("[DAO] No budget found for category '" + category + "'");
        }
        return budget;
    }

    @Override
    public void save(Budget budget) {
        storage.put(budget.getCategory(), budget);
        System.out.println("[DAO] Saved budget for category '" + budget.getCategory() +
                "': Limit=" + budget.getMonthlyLimit() + ", Spent=" + budget.getSpent());
    }

    @Override
    public void delete(String category) {
        if (storage.containsKey(category)) {
            storage.remove(category);
            System.out.println("[DAO] Deleted budget for category '" + category + "'");
        } else {
            System.out.println("[DAO] Cannot delete, no budget found for category '" + category + "'");
        }
    }

    @Override
    public Collection<Budget> findAll() {
        return storage.values();
    }
}
