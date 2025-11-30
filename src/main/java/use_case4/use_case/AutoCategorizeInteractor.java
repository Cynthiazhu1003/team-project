package use_case4.use_case;

import api.fina.FinaCategorizationGateway;
import api.fina.FinaCategorizationGateway.FinaCategorizationException;

import frontend.Transaction;

import use_case4.boundary.AutoCategorizeInputBoundary;
import use_case4.boundary.AutoCategorizeOutputBoundary;
import use_case4.data.AutoCategorizeRequestModel;
import use_case4.data.AutoCategorizeResponseModel;

import java.util.ArrayList;
import java.util.List;

public class AutoCategorizeInteractor implements AutoCategorizeInputBoundary {

    private final FinaCategorizationGateway gateway;
    private final AutoCategorizeOutputBoundary presenter;

    public AutoCategorizeInteractor(FinaCategorizationGateway gateway,
                                    AutoCategorizeOutputBoundary presenter) {
        this.gateway = gateway;
        this.presenter = presenter;
    }

    @Override
    public void execute(AutoCategorizeRequestModel requestModel) {
        try {
            List<Transaction> transactions = requestModel.getTransactions();

            // Convert domain Transactions → rows for API
            List<List<String>> rows = convertToRows(transactions);

            // Call API
            List<List<String>> categorizedRows = gateway.categorize(rows);

            // Attach category into new Transaction objects
            List<Transaction> categorized = attachCategories(transactions, categorizedRows);

            presenter.present(new AutoCategorizeResponseModel(categorized));

        } catch (FinaCategorizationException e) {
            presenter.presentFailure(e.getMessage());
        }
    }

    private List<List<String>> convertToRows(List<Transaction> transactions) {
        List<List<String>> rows = new ArrayList<>();

        for (Transaction t : transactions) {
            List<String> row = List.of(
                    t.getDescription(),   // name
                    t.getMerchant(),      // merchant
                    Double.toString(t.getAmount()) // amount
            );
            rows.add(row);
        }

        return rows;
    }

    private List<Transaction> attachCategories(List<Transaction> original,
                                               List<List<String>> categorizedRows) {

        List<Transaction> result = new ArrayList<>();

        for (int i = 0; i < categorizedRows.size(); i++) {
            Transaction base = original.get(i);
            List<String> row = categorizedRows.get(i);

            // Category is last
            String category = row.get(3);

            base.setCategory(category);

            result.add(base);
        }

        return result;
    }
}