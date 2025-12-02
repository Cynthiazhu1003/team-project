package use_case6.boundary;

import use_case6.data.GenerateCategoryReportRequestModel;

public interface GenerateCategoryReportInputBoundary {
    void generateReport(GenerateCategoryReportRequestModel requestModel);
}