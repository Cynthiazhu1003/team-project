package use_case2;

import use_case2.data_access.InMemoryTransactionDataAccessObject;
import org.junit.Before;
import org.junit.Test;
import use_case2.use_case.AddTransactionInteractor;
import use_case2.use_case.AddTransactionInputData;
import use_case2.use_case.AddTransactionOutputBoundary;
import use_case2.use_case.AddTransactionOutputData;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class AddTransactionInteractorTest {
    private AddTransactionInteractor interactor;
    private InMemoryTransactionDataAccessObject transactionDAO;
    private TestPresenter presenter;

    @Before
    public void setUp() {
        transactionDAO = new InMemoryTransactionDataAccessObject();
        presenter = new TestPresenter();
        interactor = new AddTransactionInteractor(presenter, transactionDAO);
    }

    @Test
    public void testSuccess() {
        // Given
        AddTransactionInputData inputData = new AddTransactionInputData(
                LocalDate.now(), "Dining", -5.75, "Starbucks coffee"
        );

        // When
        interactor.execute(inputData);

        // Then
        assertTrue(presenter.success);
        assertEquals(1, transactionDAO.getTransactionCount());
    }

    @Test
    public void testFailureEmptyDescription() {
        // Given
        AddTransactionInputData inputData = new AddTransactionInputData(
                LocalDate.now(), "", -5.75, "Dining"
        );

        // When
        interactor.execute(inputData);

        // Then
        assertFalse(presenter.success);
        assertEquals("Description is required", presenter.errorMessage);
    }

    @Test
    public void testFailureZeroAmount() {
        // Given
        AddTransactionInputData inputData = new AddTransactionInputData(
                LocalDate.now(), "Starbucks coffee", 0.0, "Dining"
        );

        // When
        interactor.execute(inputData);

        // Then
        assertFalse(presenter.success);
        assertEquals("Amount cannot be zero", presenter.errorMessage);
    }

    // Helper class
    private static class TestPresenter implements AddTransactionOutputBoundary {
        public boolean success = false;
        public AddTransactionOutputData outputData;
        public String errorMessage;

        @Override
        public void prepareSuccessView(AddTransactionOutputData outputData) {
            this.success = true;
            this.outputData = outputData;
        }

        @Override
        public void prepareFailView(String error) {
            this.success = false;
            this.errorMessage = error;
        }
    }
}