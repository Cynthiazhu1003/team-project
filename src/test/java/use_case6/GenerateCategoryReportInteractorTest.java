package use_case6;

import frontend.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case2.use_case.TransactionDataAccessInterface;
import use_case6.boundary.GenerateCategoryReportOutputBoundary;
import use_case6.boundary.GenerateCategoryReportRequestModel;
import use_case6.boundary.GenerateCategoryReportResponseModel;
import use_case6.use_case.GenerateCategoryReportInteractor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GenerateCategoryReportInteractor.
 *
 * These tests use:
 *  - a fake TransactionDataAccessInterface (in-memory list)
 *  - a capturing presenter that records the last response / error
 */
class GenerateCategoryReportInteractorTest {

    private FakeTransactionGateway gateway;
    private CapturingPresenter presenter;
    private GenerateCategoryReportInteractor interactor;

    // ---------- Test doubles ----------

    /** Simple in-memory implementation of TransactionDataAccessInterface. */
    private static class FakeTransactionGateway implements TransactionDataAccessInterface {

        private final List<Transaction> transactions = new ArrayList<>();

        void add(Transaction t) {
            transactions.add(t);
        }

        @Override
        public void save(Transaction transaction) {
            throw new UnsupportedOperationException("not needed for these tests");
        }

        @Override
        public List<Transaction> getAllTransactions() {
            return new ArrayList<>(transactions);
        }

        @Override
        public void update(Transaction transaction) {
            throw new UnsupportedOperationException("not needed for these tests");
        }

        @Override
        public void delete(Transaction transaction) {
            throw new UnsupportedOperationException("not needed for these tests");
        }

        @Override
        public boolean deleteByIndex(int index) {
            throw new UnsupportedOperationException("not needed for these tests");
        }
    }

    /** Presenter that just captures the last report/error instead of touching the UI. */
    private static class CapturingPresenter implements GenerateCategoryReportOutputBoundary {

        GenerateCategoryReportResponseModel lastResponse;
        String lastError;

        @Override
        public void present(GenerateCategoryReportResponseModel responseModel) {
            this.lastResponse = responseModel;
            this.lastError = null;
        }

        @Override
        public void presentError(String message) {
            this.lastError = message;
            this.lastResponse = null;
        }
    }

    // ---------- Setup ----------

    @BeforeEach
    void setUp() {
        gateway = new FakeTransactionGateway();
        presenter = new CapturingPresenter();
        interactor = new GenerateCategoryReportInteractor(gateway, presenter);
    }

    private GenerateCategoryReportRequestModel request(
            String category, int daysBack, LocalDate today) {
        return new GenerateCategoryReportRequestModel(category, daysBack, today);
    }

    // ---------- Tests ----------

    @Test
    void emptyCategoryProducesError() {
        LocalDate today = LocalDate.of(2025, 12, 1);

        interactor.generateReport(request("   ", 30, today));

        assertNull(presenter.lastResponse);
        assertEquals("Category cannot be empty.", presenter.lastError);
    }

    @Test
    void nonPositiveDaysProducesError() {
        LocalDate today = LocalDate.of(2025, 12, 1);

        interactor.generateReport(request("groceries", 0, today));

        assertNull(presenter.lastResponse);
        assertEquals("Days must be a positive number.", presenter.lastError);
    }

    @Test
    void noTransactionsInGatewayProducesNoTransactionsError() {
        LocalDate today = LocalDate.of(2025, 12, 1);

        interactor.generateReport(request("groceries", 30, today));

        assertNull(presenter.lastResponse);
        assertEquals(
                "No transactions found for category \"groceries\" in the last 30 days.",
                presenter.lastError
        );
    }

    @Test
    void transactionsExistButNoneMatchCategory() {
        LocalDate today = LocalDate.of(2025, 12, 1);

        // category "restaurants & other" only
        gateway.add(new Transaction(
                LocalDate.of(2025, 11, 20),
                "Dinner",
                "Some Restaurant",
                30.0,
                "restaurants & other"
        ));

        interactor.generateReport(request("groceries", 30, today));

        assertNull(presenter.lastResponse);
        assertEquals(
                "No transactions found for category \"groceries\" in the last 30 days.",
                presenter.lastError
        );
    }

    @Test
    void filtersByCategoryAndInclusiveDateRange() {
        LocalDate today = LocalDate.of(2025, 12, 1);       // endDate
        int daysBack = 30;                                 // startDate = 2025-11-01

        // In-range, correct category
        gateway.add(new Transaction(LocalDate.of(2025, 11, 2),
                "Milk", "Store A", 20.0, "groceries"));
        gateway.add(new Transaction(LocalDate.of(2025, 11, 30),
                "Rice", "Store B", 40.0, "groceries"));

        // Boundary: exactly on startDate
        gateway.add(new Transaction(LocalDate.of(2025, 11, 1),
                "Boundary", "Store C", 10.0, "groceries"));

        // Boundary: exactly on endDate (today)
        gateway.add(new Transaction(LocalDate.of(2025, 12, 1),
                "Today", "Store D", 5.0, "groceries"));

        // Too old: before startDate
        gateway.add(new Transaction(LocalDate.of(2025, 10, 31),
                "Too old", "Store E", 50.0, "groceries"));

        // Wrong category
        gateway.add(new Transaction(LocalDate.of(2025, 11, 15),
                "Dinner", "Some Restaurant", 15.0, "restaurants & other"));

        interactor.generateReport(request("groceries", daysBack, today));

        assertNull(presenter.lastError);
        assertNotNull(presenter.lastResponse);

        GenerateCategoryReportResponseModel resp = presenter.lastResponse;

        // Bounds
        assertEquals(LocalDate.of(2025, 11, 1), resp.getStartDate());
        assertEquals(LocalDate.of(2025, 12, 1), resp.getEndDate());
        assertEquals("groceries", resp.getCategory());

        // Count & total
        assertEquals(4, resp.getTransactionCount());
        assertEquals(75.0, resp.getTotalAmount(), 1e-9);

        // Details: each transaction summary should correspond to one of the 4 included ones
        assertEquals(4, resp.getTransactions().size());
        // you can be more specific if you want deterministic ordering:
        // e.g. check that amounts contain 20,40,10,5 in some order.
        double sumFromSummaries = resp.getTransactions()
                .stream()
                .mapToDouble(GenerateCategoryReportResponseModel.TransactionSummary::getAmount)
                .sum();
        assertEquals(75.0, sumFromSummaries, 1e-9);
    }

    @Test
    void categoryMatchIsCaseInsensitive() {
        LocalDate today = LocalDate.of(2025, 12, 1);

        gateway.add(new Transaction(
                LocalDate.of(2025, 11, 10),
                "Groceries Mixed Case",
                "Store X",
                12.34,
                "GrOcErIeS"
        ));

        interactor.generateReport(request("groceries", 30, today));

        assertNull(presenter.lastError);
        assertNotNull(presenter.lastResponse);

        GenerateCategoryReportResponseModel resp = presenter.lastResponse;
        assertEquals(1, resp.getTransactionCount());
        assertEquals(12.34, resp.getTotalAmount(), 1e-9);
    }

    @Test
    void transactionsWithNullCategoryAreIgnored() {
        LocalDate today = LocalDate.of(2025, 12, 1);

        gateway.add(new Transaction(
                LocalDate.of(2025, 11, 10),
                "Should be ignored",
                "Store NullCat",
                100.0,
                null
        ));

        gateway.add(new Transaction(
                LocalDate.of(2025, 11, 10),
                "Valid groceries",
                "Store Y",
                50.0,
                "groceries"
        ));

        interactor.generateReport(request("groceries", 30, today));

        assertNull(presenter.lastError);
        GenerateCategoryReportResponseModel resp = presenter.lastResponse;

        assertEquals(1, resp.getTransactionCount());
        assertEquals(50.0, resp.getTotalAmount(), 1e-9);
    }
}