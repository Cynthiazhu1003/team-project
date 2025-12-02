package use_case6.interface_adapter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import use_case6.data.GenerateCategoryReportResponseModel.TransactionSummary;

public class CategoryReportViewModel {

    private String category;
    private String dateRange;
    private String totalAmountText;
    private String transactionCountText;
    private List<TransactionSummary> transactions;
    private String errorMessage;

    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void setCategory(String category) {
        this.category = category;
        support.firePropertyChange("category", null, category);
    }

    public void setDateRange(String dateRange) {
        this.dateRange = dateRange;
        support.firePropertyChange("dateRange", null, dateRange);
    }

    public void setTotalAmountText(String totalAmountText) {
        this.totalAmountText = totalAmountText;
        support.firePropertyChange("totalAmountText", null, totalAmountText);
    }

    public void setTransactionCountText(String transactionCountText) {
        this.transactionCountText = transactionCountText;
        support.firePropertyChange("transactionCountText", null, transactionCountText);
    }

    public void setTransactions(List<TransactionSummary> transactions) {
        this.transactions = transactions;
        support.firePropertyChange("transactions", null, transactions);
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        support.firePropertyChange("errorMessage", null, errorMessage);
    }

    public String getCategory() {
        return category;
    }

    public String getDateRange() {
        return dateRange;
    }

    public String getTotalAmountText() {
        return totalAmountText;
    }

    public String getTransactionCountText() {
        return transactionCountText;
    }

    public List<TransactionSummary> getTransactions() {
        return transactions;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}