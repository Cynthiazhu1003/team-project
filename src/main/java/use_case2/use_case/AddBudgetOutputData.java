package use_case2.use_case;

/**
 * Output data transfer object for adding a budget.
 * Immutable DTO carrying output from interactor to presenter.
 */
public class AddBudgetOutputData {
    private final boolean success;
    private final String errorMessage;
    private final String category;
    private final double monthlyLimit;

    /**
     * Creates output data for a successful budget addition.
     *
     * @param success Whether the operation succeeded
     * @param errorMessage Error message if failed, null otherwise
     * @param category The category that was added
     * @param monthlyLimit The limit that was set
     */
    public AddBudgetOutputData(boolean success, String errorMessage,
                              String category, double monthlyLimit) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.category = category;
        this.monthlyLimit = monthlyLimit;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getCategory() {
        return category;
    }

    public double getMonthlyLimit() {
        return monthlyLimit;
    }
}
