package use_case2.use_case_deleteTrans;

public interface DeleteTransactionOutputBoundary {
    void prepareSuccessView(DeleteTransactionOutputData outputData);
    void prepareFailView(String errorMessage);
}
