package use_case5.interface_adapter;

import use_case5.boundary.GenerateCategoryReportOutputBoundary;
import use_case5.boundary.GenerateCategoryReportResponseModel;

public class GenerateCategoryReportPresenter implements GenerateCategoryReportOutputBoundary {

    private final CategoryReportViewBoundary view;

    public GenerateCategoryReportPresenter(CategoryReportViewBoundary view) {
        this.view = view;
    }

    @Override
    public void present(GenerateCategoryReportResponseModel responseModel) {
        view.showReport(responseModel);
    }

    @Override
    public void presentError(String message) {
        view.showError(message);
    }
}