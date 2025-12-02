package use_case5.boundary;

public class BudgetNotificationModel {
    public enum NotificationType { INFO, WARNING }

    public final String category;
    public final String message;
    public final NotificationType type;

    public BudgetNotificationModel(String category, String message, NotificationType type) {
        this.category = category;
        this.message = message;
        this.type = type;
    }
}