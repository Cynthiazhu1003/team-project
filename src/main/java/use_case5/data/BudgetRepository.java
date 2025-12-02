package use_case5.data;

import frontend.Budget;

import java.util.Collection;

public interface BudgetRepository {
    Budget find(String category);
    void save(Budget budget);

    void delete(String category);

    Collection<Budget> findAll();
}