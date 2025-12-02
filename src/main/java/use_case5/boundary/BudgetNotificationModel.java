package use_case5.boundary;

import frontend.Budget;

/**
 * An input data model for a budget notification, used by the budget use cases.
 */
public class BudgetNotificationModel {
    /**
     * An enum for the possible notification types.
     */
    public enum NotificationType {
        INFO, WARNING
    }

    public final String category;
    public final String message;
    public final NotificationType type;

    /**
     * Constructor for the notification model.
     */
    public BudgetNotificationModel(String category, String message, NotificationType type) {
        this.category = category;
        this.message = message;
        this.type = type;
    }

    /**
     * Returns a BudgetNotificationModel based on the parameters given, the bool fromRefresh
     * indicates whether to show the OK warning level from a screen refresh or not.
     */
    public static BudgetNotificationModel buildWarning(Budget budget, boolean fromRefresh) {
        String level = budget.getWarningLevel();
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
                        budget.getCategory(), message, NotificationType.WARNING);

            case "WARNING":
                message = String.format(
                        "Warning: You're close to exceeding your "
                                + "budget for %s.\nLimit: %.2f\nSpent: %.2f\nRemaining: %.2f",
                        budget.getCategory(), limit, spent, remaining
                );
                return new BudgetNotificationModel(
                        budget.getCategory(), message, NotificationType.WARNING);

            default:
                if (fromRefresh) {
                    return null;
                }

                message = String.format(
                        "Budget updated for %s.\nLimit: %.2f\nSpent: %.2f\nRemaining: %.2f",
                        budget.getCategory(), limit, spent, remaining
                );
                return new BudgetNotificationModel(
                        budget.getCategory(), message, NotificationType.INFO);

        }
    }
}