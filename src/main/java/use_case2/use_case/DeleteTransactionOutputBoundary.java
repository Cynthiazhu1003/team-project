package use_case2.use_case;

public interface DeleteTransactionOutputBoundary {
    void prepareSuccessView(String successMessage);
    void prepareFailView(String errorMessage);
}
