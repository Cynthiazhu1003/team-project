package use_case2.use_case;

public interface AddTransactionOutputBoundary {
    void prepareSuccessView(AddTransactionOutputData outputData);
    void prepareFailView(String errorMessage);
}
