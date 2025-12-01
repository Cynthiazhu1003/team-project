package use_case5.interface_adapter;

import use_case5.boundary.BudgetNotificationModel;
import use_case5.boundary.BudgetResponseModel;

public class BudgetViewModel {
    public BudgetResponseModel lastBudget;
    public BudgetResponseModel getBudget() { return lastBudget; }
    public void setBudget(BudgetResponseModel budget) { lastBudget = budget; }

    public BudgetNotificationModel lastNotification;
    public void setNotification(BudgetNotificationModel notif) { lastNotification = notif; }
    public BudgetNotificationModel getNotification() { return lastNotification; }
}