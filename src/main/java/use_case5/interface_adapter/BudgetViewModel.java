package use_case5.interface_adapter;

import use_case5.boundary.*;

public class BudgetViewModel {
    public void displayBudget(BudgetResponseModel model) {
        System.out.println("[Budget Updated] " + model.category +
                " | Limit: " + model.limit +
                " | Spent: " + model.spent +
                " | Remaining: " + model.remaining +
                " | Warning: " + model.warningLevel);
    }

    public void showNotification(BudgetNotificationModel notif) {
        System.out.println("[NOTIFICATION] " + notif.category + ": " + notif.message);
    }
}