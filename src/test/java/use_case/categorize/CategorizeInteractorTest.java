package use_case.categorize;

import frontend.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case3.use_case.categorize.CategorizeInputData;
import use_case3.use_case.categorize.CategorizeInteractor;
import use_case3.use_case.categorize.CategorizeOutputBoundary;
import use_case3.use_case.categorize.CategorizeOutputData;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategorizeInteractorTest {

    private List<Transaction> transactions;
    private TestPresenter presenter;
    private CategorizeInteractor interactor;

    @BeforeEach
    void setUp() {
        transactions = new ArrayList<>();

        // date, description (store), merchant (product), amount, category
        transactions.add(new Transaction(
                LocalDate.of(2024, 1, 1),
                "Store A",
                "Product A",
                10.0,
                "Uncategorized"
        ));
        transactions.add(new Transaction(
                LocalDate.of(2024, 1, 2),
                "Store B",
                "Product B",
                20.0,
                "Food"
        ));
        transactions.add(new Transaction(
                LocalDate.of(2024, 1, 3),
                "Store C",
                "Product C",
                30.0,
                "Food"
        ));

        presenter = new TestPresenter();
        interactor = new CategorizeInteractor(transactions, presenter);
    }

    /**
     * Happy path:
     * Valid index, category is updated and total is correct.
     */
    @Test
    void execute_validIndex_updatesCategoryAndTotal() {
        CategorizeInputData inputData = new CategorizeInputData(0, "Food");

        interactor.execute(inputData);

        // Transaction category actually changed
        assertEquals("Food", transactions.get(0).getCategory());

        // Presenter success called, failure not
        assertTrue(presenter.successCalled);
        assertFalse(presenter.failureCalled);
        assertNull(presenter.lastErrorMessage);

        // New total for "Food" category: 10 + 20 + 30 = 60
        assertNotNull(presenter.lastOutputData);
        assertEquals("Food", presenter.lastOutputData.getNewCategory());
        assertEquals(60.0, presenter.lastOutputData.getNewCategoryTotal(), 0.0001);
    }

    /**
     * Index too large → failure.
     */
    @Test
    void execute_indexTooLarge_triggersFailure() {
        CategorizeInputData inputData = new CategorizeInputData(5, "Food");

        interactor.execute(inputData);

        // Presenter recorded failure
        assertFalse(presenter.successCalled);
        assertTrue(presenter.failureCalled);
        assertEquals("Invalid transaction index.", presenter.lastErrorMessage);
        assertNull(presenter.lastOutputData);
    }

    /**
     * Negative index → failure.
     */
    @Test
    void execute_negativeIndex_triggersFailure() {
        CategorizeInputData inputData = new CategorizeInputData(-1, "Food");

        interactor.execute(inputData);

        assertFalse(presenter.successCalled);
        assertTrue(presenter.failureCalled);
        assertEquals("Invalid transaction index.", presenter.lastErrorMessage);
        assertNull(presenter.lastOutputData);
    }

    /**
     * Calling execute multiple times should recompute totals each time.
     */
    @Test
    void execute_multipleCalls_updatesTotalsEachTime() {
        // First: make index 0 "Food"
        interactor.execute(new CategorizeInputData(0, "Food"));
        assertEquals(60.0, presenter.lastOutputData.getNewCategoryTotal(), 0.0001);

        // Now change index 1 from "Food" to "Travel"
        interactor.execute(new CategorizeInputData(1, "Travel"));

        // Only index 1 has category "Travel", amount 20.0
        assertEquals("Travel", presenter.lastOutputData.getNewCategory());
        assertEquals(20.0, presenter.lastOutputData.getNewCategoryTotal(), 0.0001);
        assertEquals("Travel", transactions.get(1).getCategory());
    }

    /**
     * Simple stub presenter that just records what it receives.
     */
    private static class TestPresenter implements CategorizeOutputBoundary {
        CategorizeOutputData lastOutputData = null;
        String lastErrorMessage = null;
        boolean successCalled = false;
        boolean failureCalled = false;

        @Override
        public void presentSuccess(CategorizeOutputData outputData) {
            this.successCalled = true;
            this.failureCalled = false;
            this.lastOutputData = outputData;
            this.lastErrorMessage = null;
        }

        @Override
        public void presentFailure(String errorMessage) {
            this.failureCalled = true;
            this.successCalled = false;
            this.lastErrorMessage = errorMessage;
            this.lastOutputData = null;
        }
    }
}