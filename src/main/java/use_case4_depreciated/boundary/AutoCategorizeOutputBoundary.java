package use_case4_depreciated.boundary;

import use_case4_depreciated.data.AutoCategorizeResponseModel;

public interface AutoCategorizeOutputBoundary {
    void present(AutoCategorizeResponseModel responseModel);
    void presentFailure(String error);
}