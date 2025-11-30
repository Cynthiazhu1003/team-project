package use_case4;

import api.fina.FinaCategorizationGatewayImpl;
import frontend.Transaction;
import org.junit.jupiter.api.Test;
import use_case4.boundary.AutoCategorizeOutputBoundary;
import use_case4.data.AutoCategorizeRequestModel;
import use_case4.data.AutoCategorizeResponseModel;
import use_case4.interface_adapter.viewmodel.AutoCategorizeViewModel;
import use_case4.use_case.AutoCategorizeInteractor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AutoCategorizeIntegrationTest {

    @Test
    void testRealApiCategorization() {
        // Prepare real gateway
        FinaCategorizationGatewayImpl gateway = new FinaCategorizationGatewayImpl();

        // Prepare view model + presenter
        AutoCategorizeViewModel viewModel = new AutoCategorizeViewModel();
        AutoCategorizeOutputBoundary presenter = new use_case4.interface_adapter.presenter.AutoCategorizePresenter(viewModel);

        // Prepare interactor
        AutoCategorizeInteractor interactor = new AutoCategorizeInteractor(gateway, presenter);

        // Sample transactions
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(Transaction.of("2025-11-30,Coffee,Starbucks,4.5"));
        transactions.add(Transaction.of("2025-11-30,Grocery,Whole Foods,50.0"));

        // Execute use case
        AutoCategorizeRequestModel request = new AutoCategorizeRequestModel(transactions);
        interactor.execute(request);

        // Check results
        List<Transaction> result = viewModel.getCategorizedTransactions();
        String error = viewModel.getError();

        assertNull(error, "There should be no error from the API");
        assertNotNull(result, "Result should not be null");
        assertEquals(transactions.size(), result.size(), "Result should have same number of transactions");

        // Optional: print out results
        System.out.println("Categorized Transactions:");
        for (Transaction t : result) {
            System.out.println(t);
        }
    }
}