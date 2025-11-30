package use_case4.boundary;

import use_case4.data.AutoCategorizeRequestModel;

public interface AutoCategorizeInputBoundary {
    void execute(AutoCategorizeRequestModel requestModel);
}