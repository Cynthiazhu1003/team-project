package use_case6.interface_adapter;

import use_case6.boundary.GenerateCategoryReportOutputBoundary;
import use_case6.data.GenerateCategoryReportResponseModel;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class GenerateCategoryReportPresenter implements GenerateCategoryReportOutputBoundary {

    private final CategoryReportViewModel viewModel;
    private final CategoryReportViewBoundary view;

    public GenerateCategoryReportPresenter(CategoryReportViewModel viewModel,
                                           CategoryReportViewBoundary view) {
        this.viewModel = viewModel;
        this.view = view;
    }

    @Override
    public void present(GenerateCategoryReportResponseModel responseModel) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        String dateRange = responseModel.getStartDate().format(dateFormatter)
                + " to "
                + responseModel.getEndDate().format(dateFormatter);

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.CANADA);
        String totalAmountText = currencyFormatter.format(responseModel.getTotalAmount());

        String transactionCountText = responseModel.getTransactionCount() + " transactions";

        viewModel.setCategory(responseModel.getCategory());
        viewModel.setDateRange(dateRange);
        viewModel.setTotalAmountText(totalAmountText);
        viewModel.setTransactionCountText(transactionCountText);
        viewModel.setTransactions(responseModel.getTransactions());
        viewModel.setErrorMessage(null);

        view.showReport(responseModel);
    }

    @Override
    public void presentError(String message) {
        viewModel.setErrorMessage(message);
        view.showError(message);
    }
}