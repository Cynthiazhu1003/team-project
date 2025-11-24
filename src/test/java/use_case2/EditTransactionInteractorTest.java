package use_case2;

import use_case2.data_access.InMemoryTransactionDataAccessObject;
import use_case2.entity.Transaction;
import org.junit.Before;
import org.junit.Test;
import use_case2.use_case_edit_transactions.*;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class EditTransactionInteractorTest {
    private EditTransactionInteractor interactor;
    private InMemoryTransactionDataAccessObject transactionDAO;
    private TestPresenter presenter;

    @Before
    public void setUp() {
        transactionDAO = new InMemoryTransactionDataAccessObject();
        presenter = new TestPresenter();
        interactor = new EditTransactionInteractor(presenter, transactionDAO);

        // Add a sample transaction to edit
        Transaction sampleTransaction = new Transaction(
                LocalDate.now(), "Old Store", "Old Product", 10.0, "Old Category"
        );
        transactionDAO.save(sampleTransaction);
    }

    @Test
    public void testSuccess() {
        // Given - editing the first transaction (index 0)
        EditTransactionInputData inputData = new EditTransactionInputData(
                0, LocalDate.now(), "New Store", "New Product", 15.0, "New Category"
        );

        // When
        interactor.execute(inputData);

        // Then
        assertTrue(presenter.success);
        Transaction updated = transactionDAO.getAllTransactions().get(0);
        assertEquals("New Store", updated.getDescription());
        assertEquals("New Product", updated.getMerchant());
        assertEquals(15.0, updated.getAmount(), 0.001);
        assertEquals("New Category", updated.getCategory());
    }

    @Test
    public void testFailureInvalidIndex() {
        // Given - invalid index
        EditTransactionInputData inputData = new EditTransactionInputData(
                999, LocalDate.now(), "Store", "Product", 15.0, "Category"  // Invalid index
        );

        // When
        interactor.execute(inputData);

        // Then
        assertFalse(presenter.success);
        assertEquals("Invalid transaction selected", presenter.errorMessage);
    }

    @Test
    public void testFailureEmptyDescription() {
        // Given - empty description
        EditTransactionInputData inputData = new EditTransactionInputData(
                0, LocalDate.now(), "", "Product", 15.0, "Category"
        );

        // When
        interactor.execute(inputData);

        // Then
        assertFalse(presenter.success);
        assertEquals("Description (store name) is required", presenter.errorMessage);
    }

    // Helper class
    private static class TestPresenter implements EditTransactionOutputBoundary {
        public boolean success = false;
        public EditTransactionOutputData outputData;
        public String errorMessage;

        @Override
        public void prepareSuccessView(EditTransactionOutputData outputData) {
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
