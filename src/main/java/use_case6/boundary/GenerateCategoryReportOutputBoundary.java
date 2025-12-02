package use_case6.boundary;

import use_case6.data.GenerateCategoryReportResponseModel;

public interface GenerateCategoryReportOutputBoundary {
    void present(GenerateCategoryReportResponseModel responseModel);
    void presentError(String message);
}