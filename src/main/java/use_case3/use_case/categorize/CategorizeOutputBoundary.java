package use_case3.use_case.categorize;

public interface CategorizeOutputBoundary {
    void presentSuccess(CategorizeOutputData outputData);
    void presentFailure(String errorMessage);
}