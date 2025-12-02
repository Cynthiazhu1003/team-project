package use_case5.boundary;

/**
 * Output boundary for budget use cases.
 */
public interface BudgetOutputBoundary {
    /**
     * Formats the responseModel given into a budget table property changed evt for the view model.
     */
    void presentBudget(BudgetResponseModel responseModel);

    /**
     * Formats the notificationModel given into a show notification property changed evt for the view model.
     */
    void presentNotification(BudgetNotificationModel notificationModel);
}