package use_case5.boundary;

public interface BudgetOutputBoundary {
    void presentBudget(BudgetResponseModel responseModel);
    void presentNotification(BudgetNotificationModel notificationModel);
}