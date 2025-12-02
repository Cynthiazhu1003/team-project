package use_case2.use_case_editTrans;

public class EditTransactionOutputData {
    private final boolean success;
    private final String errorMessage;

    public EditTransactionOutputData(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() { return success; }
    public String getErrorMessage() { return errorMessage; }
}