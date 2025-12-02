package use_case2;

import frontend.Transaction;
import use_case2.data_access.InMemoryTransactionDataAccessObject;
import org.junit.Before;
import org.junit.Test;
import use_case2.use_case_editTrans.*;

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

        Transaction sampleTransaction = new Transaction(
                LocalDate.now(), "Old Store", "Old Product", 10.0, "Old Category"
        );
        transactionDAO.save(sampleTransaction);
    }

    @Test
    public void testSuccess() {

        EditTransactionInputData inputData = new EditTransactionInputData(
                0, LocalDate.now(), "New Store", "New Product", 15.0, "New Category"
        );


        interactor.execute(inputData);


        assertTrue(presenter.success);
        assertNotNull(presenter.outputData);
        assertTrue(presenter.outputData.isSuccess());
        assertNull(presenter.outputData.getErrorMessage());

        Transaction updated = transactionDAO.getAllTransactions().get(0);
        assertEquals("New Store", updated.getDescription());
        assertEquals("New Product", updated.getMerchant());
        assertEquals(15.0, updated.getAmount(), 0.001);
        assertEquals("New Category", updated.getCategory());
    }

    @Test
    public void testFailureInvalidIndexNegative() {
        // Given - negative index
        EditTransactionInputData inputData = new EditTransactionInputData(
                -1, LocalDate.now(), "Store", "Product", 15.0, "Category"
        );

        // When
        interactor.execute(inputData);

        // Then
        assertFalse(presenter.success);
        assertEquals("Invalid transaction selected", presenter.errorMessage);
        // Verify original transaction unchanged
        Transaction original = transactionDAO.getAllTransactions().get(0);
        assertEquals("Old Store", original.getDescription());
    }



    @Test
    public void testFailureInvalidIndexTooHigh() {
        EditTransactionInputData inputData = new EditTransactionInputData(
                1, LocalDate.now(), "Store", "Product", 15.0, "Category"
        );

        interactor.execute(inputData);

        assertFalse(presenter.success);
        assertEquals("Invalid transaction selected", presenter.errorMessage);
    }

    @Test
    public void testFailureEmptyDescription() {

        EditTransactionInputData inputData = new EditTransactionInputData(
                0, LocalDate.now(), "", "Product", 15.0, "Category"
        );


        interactor.execute(inputData);

        assertFalse(presenter.success);
        assertEquals("Please fill in the Store Name.", presenter.errorMessage);

        Transaction original = transactionDAO.getAllTransactions().get(0);
        assertEquals("Old Store", original.getDescription());
    }

    @Test
    public void testFailureNullDescription() {

        EditTransactionInputData inputData = new EditTransactionInputData(
                0, LocalDate.now(), null, "Product", 15.0, "Category"
        );

        interactor.execute(inputData);

        assertFalse(presenter.success);
        assertEquals("Please fill in the Store Name.", presenter.errorMessage);
    }

    @Test
    public void testFailureDescriptionWithOnlySpaces() {
        EditTransactionInputData inputData = new EditTransactionInputData(
                0, LocalDate.now(), "   ", "Product", 15.0, "Category"
        );

        interactor.execute(inputData);

        assertFalse(presenter.success);
        assertEquals("Please fill in the Store Name.", presenter.errorMessage);
    }

    @Test
    public void testFailureZeroAmount() {
        EditTransactionInputData inputData = new EditTransactionInputData(
                0, LocalDate.now(), "Store", "Product", 0.0, "Category"
        );

        interactor.execute(inputData);

        assertFalse(presenter.success);
        assertEquals("Please enter a non-zero Amount", presenter.errorMessage);
        Transaction original = transactionDAO.getAllTransactions().get(0);
        assertEquals(10.0, original.getAmount(), 0.001);
    }

    @Test
    public void testExceptionHandling_UpdateThrowsException() {
        InMemoryTransactionDataAccessObject failingDAO = new InMemoryTransactionDataAccessObject() {
            @Override
            public void update(Transaction transaction) {
                throw new RuntimeException("Database update failed");
            }

            @Override
            public void save(Transaction transaction) {
                super.save(transaction);
            }

            @Override
            public java.util.List<Transaction> getAllTransactions() {
                java.util.List<Transaction> list = new java.util.ArrayList<>();
                list.add(new Transaction(LocalDate.now(), "Test", "Test", 10.0, "Test"));
                return list;
            }
        };

        Transaction sample = new Transaction(LocalDate.now(), "Old", "Old", 10.0, "Old");
        failingDAO.save(sample);

        interactor = new EditTransactionInteractor(presenter, failingDAO);

        EditTransactionInputData inputData = new EditTransactionInputData(
                0, LocalDate.now(), "New Store", "New Product", 15.0, "New Category"
        );

        interactor.execute(inputData);

        assertFalse(presenter.success);
        assertTrue(presenter.errorMessage.contains("Failed to edit transaction:"));
        assertTrue(presenter.errorMessage.contains("Database update failed"));
    }

    @Test
    public void testSuccessEditOnlySomeFields() {
        EditTransactionInputData inputData = new EditTransactionInputData(
                0, LocalDate.now(), "Updated Store", "Old Product", 10.0, "Old Category"
        );

        interactor.execute(inputData);

        assertTrue(presenter.success);
        Transaction updated = transactionDAO.getAllTransactions().get(0);
        assertEquals("Updated Store", updated.getDescription());
        assertEquals("Old Product", updated.getMerchant()); // unchanged
        assertEquals(10.0, updated.getAmount(), 0.001); // unchanged
        assertEquals("Old Category", updated.getCategory()); // unchanged
    }

    @Test
    public void testMultipleTransactionsEditSecond() {
        Transaction secondTransaction = new Transaction(
                LocalDate.now().minusDays(1), "Second Store", "Second Product", 20.0, "Second Category"
        );
        transactionDAO.save(secondTransaction);

        EditTransactionInputData inputData = new EditTransactionInputData(
                1, LocalDate.now(), "Edited Second", "Edited Product", 25.0, "Edited Category"
        );

        interactor.execute(inputData);

        assertTrue(presenter.success);

        Transaction first = transactionDAO.getAllTransactions().get(0);
        assertEquals("Old Store", first.getDescription());

        Transaction second = transactionDAO.getAllTransactions().get(1);
        assertEquals("Edited Second", second.getDescription());
        assertEquals(25.0, second.getAmount(), 0.001);
    }

    @Test
    public void testOutputDataOnSuccess() {
        EditTransactionInputData inputData = new EditTransactionInputData(
                0, LocalDate.now(), "Store", "Product", 15.0, "Category"
        );
        interactor.execute(inputData);

        assertTrue(presenter.success);
        assertNotNull(presenter.outputData);
        assertTrue(presenter.outputData.isSuccess());
        assertNull(presenter.outputData.getErrorMessage());
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
