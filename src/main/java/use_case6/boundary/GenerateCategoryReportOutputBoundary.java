package use_case6.boundary;

public interface GenerateCategoryReportOutputBoundary {
    void present(GenerateCategoryReportResponseModel responseModel);
    void presentError(String message);
}