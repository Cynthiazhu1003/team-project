package use_case5.data;

import frontend.Budget;
import java.util.HashMap;
import java.util.Map;

public class InMemoryBudgetRepository implements BudgetRepository {

    private final Map<String, Budget> storage = new HashMap<>();

    @Override
    public Budget find(String category) {
        return storage.get(category);
    }

    @Override
    public void save(Budget budget) {
        storage.put(budget.getCategory(), budget);
    }
}