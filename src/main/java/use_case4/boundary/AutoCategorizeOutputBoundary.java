package use_case4.boundary;

import use_case4.data.AutoCategorizeResponseModel;

public interface AutoCategorizeOutputBoundary {
    void present(AutoCategorizeResponseModel responseModel);
    void presentFailure(String error);
}