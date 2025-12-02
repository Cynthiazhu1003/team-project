package use_case2.interface_adapter.transaction_Managment;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class ViewModel<T> {
    private final String viewName;
    private T state;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public ViewModel(String viewName) { this.viewName = viewName; }
    public String getViewName() { return viewName; }
    public T getState() { return state; }
    public void setState(T state) { this.state = state; }
    public void firePropertyChanged() { support.firePropertyChange("state", null, this.state); }
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
