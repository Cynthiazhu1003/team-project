package use_case5.boundary;

public interface GenerateCategoryReportOutputBoundary {
    void present(GenerateCategoryReportResponseModel responseModel);
    void presentError(String message);
}