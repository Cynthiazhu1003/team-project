package use_case2.use_case;

public interface DeleteTransactionOutputBoundary {
    void prepareSuccessView(DeleteTransactionOutputData outputData);
    void prepareFailView(String errorMessage);
}
