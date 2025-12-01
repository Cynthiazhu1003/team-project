package use_case6.interface_adapter;

import use_case6.boundary.GenerateCategoryReportResponseModel;

public interface CategoryReportViewBoundary {
    void showReport(GenerateCategoryReportResponseModel responseModel);
    void showError(String message);
}