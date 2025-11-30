package use_case5.data;

import frontend.Budget;

public interface BudgetRepository {
    Budget find(String category);
    void save(Budget budget);
}