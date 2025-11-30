package use_case3.interface_adapter.categorize;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

public class CategorizeViewModel {

    public static final String MESSAGE_PROPERTY = "message";
    public static final String TOTALS_PROPERTY = "categoryTotals";

    // category name -> total amount in that category
    private Map<String, Double> categoryTotals = new HashMap<>();

    // a status or error message for the UI to display
    private String message = "";

    // 1. The "Megaphone" that manages listeners (Observers)
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    // --- Getters ---

    public Map<String, Double> getCategoryTotals() {
        return categoryTotals;
    }

    public String getMessage() {
        return message;
    }

    // --- Setters (Now with Notifications) ---

    public void setCategoryTotal(String category, double total) {
        // Update the data
        categoryTotals.put(category, total);

        // 2. Fire an event!
        support.firePropertyChange(TOTALS_PROPERTY, null, this.categoryTotals);
    }

    public void setMessage(String message) {
        String oldMessage = this.message;
        this.message = message;

        // 2. Fire an event!
        // The View will hear "message" changed from 'oldMessage' to 'message'
        support.firePropertyChange(MESSAGE_PROPERTY, oldMessage, message);
    }

    // --- Observer Management (This is what fixes the red error) ---

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}