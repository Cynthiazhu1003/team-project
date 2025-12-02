package use_case2.use_case_addTrans;

public interface AddTransactionOutputBoundary {
    void prepareSuccessView(AddTransactionOutputData outputData);
    void prepareFailView(String errorMessage);
}
