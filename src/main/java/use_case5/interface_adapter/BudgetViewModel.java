package use_case5.interface_adapter;

import use_case5.boundary.BudgetResponseModel;
import use_case5.boundary.BudgetNotificationModel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class BudgetViewModel {

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private BudgetResponseModel lastBudget;
    private BudgetNotificationModel lastNotification;

    public static final String PROP_BUDGET = "budget";
    public static final String PROP_NOTIFICATION = "notification";

    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public BudgetResponseModel getBudget() { return lastBudget; }

    public void setBudget(BudgetResponseModel budget) {
        BudgetResponseModel old = this.lastBudget;
        this.lastBudget = budget;
        pcs.firePropertyChange(PROP_BUDGET, old, budget);
    }

    public BudgetNotificationModel getNotification() { return lastNotification; }

    public void setNotification(BudgetNotificationModel notif) {
        BudgetNotificationModel old = this.lastNotification;
        this.lastNotification = notif;
        pcs.firePropertyChange(PROP_NOTIFICATION, old, notif);
    }
}