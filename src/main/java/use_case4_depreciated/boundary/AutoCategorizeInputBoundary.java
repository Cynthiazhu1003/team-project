package use_case4_depreciated.boundary;

import use_case4_depreciated.data.AutoCategorizeRequestModel;

public interface AutoCategorizeInputBoundary {
    void execute(AutoCategorizeRequestModel requestModel);
}