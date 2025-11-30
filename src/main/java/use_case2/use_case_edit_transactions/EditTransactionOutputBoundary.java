package use_case2.use_case_edit_transactions;

public interface EditTransactionOutputBoundary {
    void prepareSuccessView(EditTransactionOutputData outputData);
    void prepareFailView(String error);
}