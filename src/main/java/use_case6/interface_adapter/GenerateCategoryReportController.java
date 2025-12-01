package use_case6.interface_adapter;

import java.time.LocalDate;

import use_case6.boundary.GenerateCategoryReportInputBoundary;
import use_case6.boundary.GenerateCategoryReportRequestModel;

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
