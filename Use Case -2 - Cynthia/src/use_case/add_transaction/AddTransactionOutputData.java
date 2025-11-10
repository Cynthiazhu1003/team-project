package use_case.add_transaction;

public class AddTransactionOutputData {
    private final boolean success;
    private final String errorMessage;

    public AddTransactionOutputData(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }


    public boolean isSuccess() { return success; }
    public String getErrorMessage() { return errorMessage; }
}
