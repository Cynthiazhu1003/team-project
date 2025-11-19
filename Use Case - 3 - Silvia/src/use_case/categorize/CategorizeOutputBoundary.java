public interface CategorizeOutputBoundary {
    void presentSuccess(CategorizeOutputData outputData);
    void presentFailure(String errorMessage);
}