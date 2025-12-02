package use_case2;

import frontend.Transaction;
import use_case2.data_access.InMemoryTransactionDataAccessObject;
import org.junit.Before;
import org.junit.Test;
import use_case2.use_case_addTrans.AddTransactionInteractor;
import use_case2.use_case_addTrans.AddTransactionInputData;
import use_case2.use_case_addTrans.AddTransactionOutputBoundary;
import use_case2.use_case_addTrans.AddTransactionOutputData;

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
        AddTransactionInputData inputData = new AddTransactionInputData(
                LocalDate.now(), "Starbucks","Latte", -5.75, "Drinks"
        );

        interactor.execute(inputData);

        assertTrue(presenter.success);
        assertNotNull(presenter.outputData);
        assertTrue(presenter.outputData.isSuccess());
        assertNull(presenter.outputData.getErrorMessage());
        assertEquals(1, transactionDAO.getTransactionCount());
    }

    @Test
    public void testFailureEmptyDescription() {
        AddTransactionInputData inputData = new AddTransactionInputData(
                LocalDate.now(), "", "Latte",-5.75, "Drinks"
        );

        interactor.execute(inputData);

        assertFalse(presenter.success);
        assertEquals("Please fill in the Store Name", presenter.errorMessage);
        assertEquals(0, transactionDAO.getTransactionCount());
    }

    @Test
    public void testFailureNullDescription() {
        AddTransactionInputData inputData = new AddTransactionInputData(
                LocalDate.now(), null, "Latte", 5.75, "Drinks"
        );

        interactor.execute(inputData);

        assertFalse(presenter.success);
        assertEquals("Please fill in the Store Name", presenter.errorMessage);
    }

    @Test
    public void testFailureDescriptionWithOnlySpaces() {
        AddTransactionInputData inputData = new AddTransactionInputData(
                LocalDate.now(), "   ", "Latte", 5.75, "Drinks"
        );

        interactor.execute(inputData);

        assertFalse(presenter.success);
        assertEquals("Please fill in the Store Name", presenter.errorMessage);
    }

    @Test
    public void testFailureZeroAmount() {
        AddTransactionInputData inputData = new AddTransactionInputData(
                LocalDate.now(), "Starbucks", "Latte",0.0, "Dining"
        );

        interactor.execute(inputData);

        assertFalse(presenter.success);
        assertEquals("Please enter a non-zero Amount.", presenter.errorMessage);
        assertEquals(0, transactionDAO.getTransactionCount());
    }

    @Test
    public void testFailureNegativeAmount() {
        AddTransactionInputData inputData = new AddTransactionInputData(
                LocalDate.now(), "Starbucks", "Latte", -5.75, "Drinks"
        );

        interactor.execute(inputData);

        assertTrue(presenter.success);
        assertEquals(1, transactionDAO.getTransactionCount());
    }

    @Test
    public void testFailureEmptyMerchant() {
        AddTransactionInputData inputData = new AddTransactionInputData(
                LocalDate.now(), "Starbucks", "", -5.75, "Dining"  // Empty merchant
        );


        interactor.execute(inputData);

        assertFalse(presenter.success);
        assertEquals("Merchant is required", presenter.errorMessage);
        assertEquals(0, transactionDAO.getTransactionCount());
    }
    @Test
    public void testFailureNullMerchant() {

        AddTransactionInputData inputData = new AddTransactionInputData(
                LocalDate.now(), "Starbucks", null, 5.75, "Dining"
        );

        interactor.execute(inputData);

        assertFalse(presenter.success);
        assertEquals("Merchant is required", presenter.errorMessage);
    }

    @Test
    public void testFailureMerchantWithOnlySpaces() {
        AddTransactionInputData inputData = new AddTransactionInputData(
                LocalDate.now(), "Starbucks", "   ", 5.75, "Dining"
        );

        interactor.execute(inputData);

        assertFalse(presenter.success);
        assertEquals("Merchant is required", presenter.errorMessage);
    }


    @Test
    public void testMultipleSuccessTransactions() {
        AddTransactionInputData input1 = new AddTransactionInputData(
                LocalDate.now(), "Store1", "Merchant1", 10.0, "Cat1"
        );
        AddTransactionInputData input2 = new AddTransactionInputData(
                LocalDate.now(), "Store2", "Merchant2", 20.0, "Cat2"
        );

        interactor.execute(input1);
        interactor.execute(input2);

        assertEquals(2, transactionDAO.getTransactionCount());
    }

    @Test
    public void testOutputDataContents() {
        AddTransactionInputData inputData = new AddTransactionInputData(
                LocalDate.now(), "Amazon", "Online", 99.99, "Shopping"
        );


        interactor.execute(inputData);
        assertTrue(presenter.success);
        assertNotNull(presenter.outputData);
        assertTrue(presenter.outputData.isSuccess());
        assertNull(presenter.outputData.getErrorMessage());
    }

    @Test
    public void testExceptionHandling_SaveThrowsException() {
        // Create a spy/wrapper that extends your real DAO
        InMemoryTransactionDataAccessObject failingDAO = new InMemoryTransactionDataAccessObject() {
            @Override
            public void save(Transaction transaction) {
                throw new RuntimeException("Database connection failed");
            }
        };

        interactor = new AddTransactionInteractor(presenter, failingDAO);

        AddTransactionInputData inputData = new AddTransactionInputData(
                LocalDate.now(), "Test Store", "Test Merchant", 10.0, "Test Category"
        );

        // When
        interactor.execute(inputData);

        // Then
        assertFalse(presenter.success);
        assertTrue(presenter.errorMessage.contains("Failed to add transaction:"));
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