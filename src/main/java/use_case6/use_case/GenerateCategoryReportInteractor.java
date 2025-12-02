package use_case6.use_case;

import frontend.Transaction;
import use_case2.use_case.TransactionDataAccessInterface;
import use_case6.boundary.GenerateCategoryReportInputBoundary;
import use_case6.boundary.GenerateCategoryReportOutputBoundary;
import use_case6.data.GenerateCategoryReportRequestModel;
import use_case6.data.GenerateCategoryReportResponseModel;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class GenerateCategoryReportInteractor implements GenerateCategoryReportInputBoundary {

    private final TransactionDataAccessInterface transactionGateway;
    private final GenerateCategoryReportOutputBoundary presenter;

    public GenerateCategoryReportInteractor(TransactionDataAccessInterface transactionGateway,
                                            GenerateCategoryReportOutputBoundary presenter) {
        this.transactionGateway = transactionGateway;
        this.presenter = presenter;
    }

    @Override
    public void generateReport(GenerateCategoryReportRequestModel requestModel) {
        String category = requestModel.getCategory();
        int daysBack = requestModel.getDaysBack();

        if (category == null || category.trim().isEmpty()) {
            presenter.presentError("Category cannot be empty.");
            return;
        }
        if (daysBack <= 0) {
            presenter.presentError("Days must be a positive number.");
            return;
        }

        LocalDate endDate = requestModel.getToday();
        LocalDate startDate = endDate.minusDays(daysBack);

        // ✅ Use existing DAO, no changes to use_case2:
        List<Transaction> all = transactionGateway.getAllTransactions();

        List<Transaction> matching = all.stream()
                .filter(t -> t.getCategory() != null)
                .filter(t -> t.getCategory().equalsIgnoreCase(category))
                .filter(t -> {
                    LocalDate d = t.getDate();
                    boolean afterOrEqualStart = d.isEqual(startDate) || d.isAfter(startDate);
                    boolean beforeOrEqualEnd = d.isEqual(endDate) || d.isBefore(endDate);
                    return afterOrEqualStart && beforeOrEqualEnd;
                })
                .collect(Collectors.toList());

        if (matching.isEmpty()) {
            presenter.presentError("No transactions found for category \"" + category +
                    "\" in the last " + daysBack + " days.");
            return;
        }

        double total = matching.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();

        List<GenerateCategoryReportResponseModel.TransactionSummary> summaries =
                matching.stream()
                        .map(t -> new GenerateCategoryReportResponseModel.TransactionSummary(
                                t.getDate(),
                                t.getDescription(),
                                t.getAmount()
                        ))
                        .collect(Collectors.toList());

        GenerateCategoryReportResponseModel response =
                new GenerateCategoryReportResponseModel(
                        category,
                        startDate,
                        endDate,
                        total,
                        matching.size(),
                        summaries
                );

        presenter.present(response);
    }
}