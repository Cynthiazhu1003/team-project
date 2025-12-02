package use_case5.use_case;

import use_case5.boundary.*;
import use_case5.data.BudgetRepository;
import frontend.Budget;
import frontend.Transaction;
import use_case2.use_case.TransactionDataAccessInterface;

import java.time.LocalDate;
import java.util.List;

public class BudgetInteractor implements BudgetInputBoundary {

    private final BudgetRepository repository;
    private final BudgetOutputBoundary presenter;
    private final TransactionDataAccessInterface transactionDAO;

    public BudgetInteractor(BudgetRepository repository,
                            TransactionDataAccessInterface transactionDAO,
                            BudgetOutputBoundary presenter) {
        this.repository = repository;
        this.transactionDAO = transactionDAO;
        this.presenter = presenter;
    }

    @Override
    public void setBudget(SetBudgetRequestModel request) {
        Budget budget = new Budget(request.category, request.limit, request.month);

        double spent = calculateSpent(request.category);
        budget.setSpent(spent);

        repository.save(budget);

        presenter.presentBudget(buildResponse(budget));
        presenter.presentNotification(buildWarning(budget));
    }

    @Override
    public void addSpending(UpdateBudgetRequestModel request) {
        Budget budget = repository.find(request.category);

        if (budget == null) {
            throw new RuntimeException("Budget does not exist for category: " + request.category);
        }

        double spent = calculateSpent(request.category);
        budget.setSpent(spent);

        repository.save(budget);

        presenter.presentBudget(buildResponse(budget));
        presenter.presentNotification(buildWarning(budget));
    }

    @Override
    public double calculateSpent(String category) {
        List<Transaction> all = transactionDAO.getAllTransactions();

        LocalDate firstDayOfCurrentMonth = LocalDate.now().withDayOfMonth(1);

        return all.stream()
                .filter(t -> t.getCategory() != null && t.getCategory().equals(category))
                .filter(t ->
                        t.getDate().isEqual(firstDayOfCurrentMonth) ||
                                t.getDate().isAfter(firstDayOfCurrentMonth)
                )
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    private BudgetResponseModel buildResponse(Budget budget) {
        BudgetResponseModel res = new BudgetResponseModel();
        res.category = budget.getCategory();
        res.limit = budget.getMonthlyLimit();
        res.spent = budget.getSpent();
        res.remaining = budget.getRemaining();
        res.warningLevel = budget.getWarningLevel();
        return res;
    }

    public void deleteBudget(String category) {
        Budget budget = repository.find(category);

        if (budget == null) {
            throw new RuntimeException("Budget does not exist for category: " + category);
        }

        repository.delete(category);

        // 1️⃣ Send a budget update to remove the row
        BudgetResponseModel deletedBudget = new BudgetResponseModel();
        deletedBudget.category = category;
        deletedBudget.limit = 0;
        deletedBudget.spent = 0;
        deletedBudget.remaining = 0;
        deletedBudget.warningLevel = "DELETED";

        presenter.presentBudget(deletedBudget);

        // 2️⃣ Send a notification
        BudgetNotificationModel notif = new BudgetNotificationModel(
                category,
                "Budget for " + category + " has been deleted.",
                BudgetNotificationModel.NotificationType.INFO
        );

        presenter.presentNotification(notif);
    }

    public void refreshAllBudgets() {
        for (Budget budget : repository.findAll()) {

            double spent = calculateSpent(budget.getCategory());
            budget.setSpent(spent);
            presenter.presentBudget(buildResponse(budget));
            presenter.presentNotification(buildWarning(budget));
        }
    }

    private BudgetNotificationModel buildWarning(Budget budget) {

        String level = budget.getWarningLevel();  // e.g., "EXCEEDED", "WARNING", "OK"
        double spent = budget.getSpent();
        double limit = budget.getMonthlyLimit();
        double remaining = budget.getRemaining();

        String message;

        switch (level) {
            case "EXCEEDED":
                message = String.format(
                        "Budget exceeded for %s!\nLimit: %.2f\nSpent: %.2f\nOver by: %.2f",
                        budget.getCategory(), limit, spent, Math.abs(remaining)
                );
                return new BudgetNotificationModel(
                        budget.getCategory(),
                        message,
                        BudgetNotificationModel.NotificationType.WARNING
                );

            case "WARNING":
                message = String.format(
                        "Warning: You're close to exceeding your budget for %s.\nLimit: %.2f\nSpent: %.2f\nRemaining: %.2f",
                        budget.getCategory(), limit, spent, remaining
                );
                return new BudgetNotificationModel(
                        budget.getCategory(),
                        message,
                        BudgetNotificationModel.NotificationType.WARNING
                );

            default:
                message = String.format(
                        "Budget updated for %s.\nLimit: %.2f\nSpent: %.2f\nRemaining: %.2f",
                        budget.getCategory(), limit, spent, remaining
                );
                return new BudgetNotificationModel(
                        budget.getCategory(),
                        message,
                        BudgetNotificationModel.NotificationType.INFO
                );
        }
    }
}