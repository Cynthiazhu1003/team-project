package use_case5.interface_adapter;

import use_case5.boundary.GenerateCategoryReportResponseModel;

public interface CategoryReportViewBoundary {
    void showReport(GenerateCategoryReportResponseModel responseModel);
    void showError(String message);
}