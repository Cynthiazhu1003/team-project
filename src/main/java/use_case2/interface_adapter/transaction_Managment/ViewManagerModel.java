package use_case2.interface_adapter.transaction_Managment;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ViewManagerModel {
    private String activeViewName;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public String getActiveView() { return activeViewName; }
    public void setActiveView(String activeViewName) { this.activeViewName = activeViewName; }
    public void firePropertyChanged() { support.firePropertyChange("view", null, this.activeViewName); }
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}