package use_case6.boundary;

import java.time.LocalDate;

public class GenerateCategoryReportRequestModel {
    private final String category;
    private final int daysBack;
    private final LocalDate today;

    public GenerateCategoryReportRequestModel(String category, int daysBack, LocalDate today) {
        this.category = category;
        this.daysBack = daysBack;
        this.today = today;
    }

    public String getCategory() {
        return category;
    }

    public int getDaysBack() {
        return daysBack;
    }

    public LocalDate getToday() {
        return today;
    }
}