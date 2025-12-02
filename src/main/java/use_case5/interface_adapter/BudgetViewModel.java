package use_case5.interface_adapter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import use_case5.boundary.BudgetNotificationModel;
import use_case5.boundary.BudgetResponseModel;

/**
 * View model for the Budget use cases.
 */
public class BudgetViewModel {
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private BudgetResponseModel lastBudget;
    private BudgetNotificationModel lastNotification;

    public static final String PROP_BUDGET = "budget";
    public static final String PROP_NOTIFICATION = "notification";

    /**
     * Adds a property change listener to the view model.
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public BudgetResponseModel getBudget() {
        return lastBudget;
    }

    /**
     * Fires a budget table property change.
     */
    public void setBudget(BudgetResponseModel budget) {
        BudgetResponseModel old = this.lastBudget;
        this.lastBudget = budget;
        pcs.firePropertyChange(PROP_BUDGET, old, budget);
    }

    public BudgetNotificationModel getNotification() {
        return lastNotification;
    }

    /**
     * Fires a show notification property change.
     */
    public void setNotification(BudgetNotificationModel notif) {
        BudgetNotificationModel old = this.lastNotification;
        this.lastNotification = notif;
        pcs.firePropertyChange(PROP_NOTIFICATION, old, notif);
    }
}