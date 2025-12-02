package use_case2.use_case;

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
