package use_case5.interface_adapter;

import use_case5.boundary.GenerateCategoryReportInputBoundary;
import use_case5.boundary.GenerateCategoryReportRequestModel;

import java.time.LocalDate;

public class GenerateCategoryReportController {

    private final GenerateCategoryReportInputBoundary interactor;

    public GenerateCategoryReportController(GenerateCategoryReportInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void generate(String category, int daysBack) {
        GenerateCategoryReportRequestModel request =
                new GenerateCategoryReportRequestModel(category, daysBack, LocalDate.now());
        interactor.generateReport(request);
    }
}
