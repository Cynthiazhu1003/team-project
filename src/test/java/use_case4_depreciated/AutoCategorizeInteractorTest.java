package use_case4_depreciated;

import api.fina.FinaCategorizationGateway;
import frontend.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case4_depreciated.boundary.AutoCategorizeOutputBoundary;
import use_case4_depreciated.data.AutoCategorizeRequestModel;
import use_case4_depreciated.interface_adapter.viewmodel.AutoCategorizeViewModel;
import use_case4_depreciated.use_case.AutoCategorizeInteractor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AutoCategorizeInteractorTest {

    private AutoCategorizeInteractor interactor;
    private MockGateway mockGateway;
    private AutoCategorizeViewModel viewModel;

    @BeforeEach
    void setUp() {
        mockGateway = new MockGateway();
        viewModel = new AutoCategorizeViewModel();
        AutoCategorizeOutputBoundary presenter = new use_case4_depreciated.interface_adapter.presenter.AutoCategorizePresenter(viewModel);
        interactor = new AutoCategorizeInteractor(mockGateway, presenter);
    }

    @Test
    void testCategorizationSuccess() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(LocalDate.of(2025,11,30),"Coffee","Starbucks",4.5));
        transactions.add(new Transaction(LocalDate.of(2025,11,30),"Groceries","Whole Foods",50.0));

        AutoCategorizeRequestModel request = new AutoCategorizeRequestModel(transactions);

        interactor.execute(request);

        // check presenter/view model received categorized transactions
        List<Transaction> result = viewModel.getCategorizedTransactions();
        assertNotNull(result);
        assertEquals(2, result.size());

        // verify gateway was called
        assertTrue(mockGateway.wasCalled());
    }

    @Test
    void testCategorizationFailure() {
        mockGateway.setThrowException(true);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(LocalDate.of(2025,11,30),"Lunch","McDonalds",10.0));

        AutoCategorizeRequestModel request = new AutoCategorizeRequestModel(transactions);

        interactor.execute(request);

        assertNull(viewModel.getCategorizedTransactions());
        assertNotNull(viewModel.getError());
        assertEquals("Gateway failed", viewModel.getError());
    }

    // Mock Gateway
    static class MockGateway implements FinaCategorizationGateway {

        private boolean throwException = false;
        private boolean called = false;

        public void setThrowException(boolean throwException) {
            this.throwException = throwException;
        }

        public boolean wasCalled() {
            return called;
        }

        @Override
        public List<List<String>> categorize(List<List<String>> rows) throws FinaCategorizationException {
            called = true;
            if (throwException) {
                throw new FinaCategorizationException("Gateway failed");
            }

            List<List<String>> result = new ArrayList<>();
            for (List<String> row : rows) {
                List<String> newRow = new ArrayList<>(row);
                newRow.add("TestCategory"); // mock category
                result.add(newRow);
            }
            return result;
        }
    }
}