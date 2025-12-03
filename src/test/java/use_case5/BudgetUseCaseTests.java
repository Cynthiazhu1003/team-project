package use_case5;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import use_case5.boundary.*;
import use_case5.data.InMemoryBudgetRepository;
import use_case2.data_access.InMemoryTransactionDataAccessObject;

import frontend.Transaction;

import use_case5.use_case.SetBudgetInteractor;
import use_case5.use_case.DeleteBudgetInteractor;
import use_case5.use_case.RefreshBudgetsInteractor;
import use_case5.use_case.BudgetSpentCalculator;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

class BudgetUseCaseTests {

    private InMemoryBudgetRepository budgetRepo;
    private InMemoryTransactionDataAccessObject txnDao;
    private BudgetSpentCalculator calculator;
    private TestPresenter presenter;

    private SetBudgetInteractor setInteractor;
    private DeleteBudgetInteractor deleteInteractor;
    private RefreshBudgetsInteractor refreshInteractor;

    // Test Presenter
    static class TestPresenter implements BudgetOutputBoundary {
        BudgetResponseModel lastResponse;
        BudgetNotificationModel lastNotification;

        @Override
        public void presentBudget(BudgetResponseModel responseModel) {
            lastResponse = responseModel;
        }

        @Override
        public void presentNotification(BudgetNotificationModel notificationModel) {
            lastNotification = notificationModel;
        }
    }

    // Setup
    @BeforeEach
    void setup() {
        budgetRepo = new InMemoryBudgetRepository();
        txnDao = new InMemoryTransactionDataAccessObject();
        calculator = new BudgetSpentCalculator(txnDao);
        presenter = new TestPresenter();

        setInteractor = new SetBudgetInteractor(budgetRepo, calculator, presenter);
        deleteInteractor = new DeleteBudgetInteractor(budgetRepo, presenter);
        refreshInteractor = new RefreshBudgetsInteractor(budgetRepo, calculator, presenter);
    }

    @Test
    void testSetBudgetWithExistingTransactions() {

        // Create two transactions that fall in the current month
        txnDao.save(new Transaction(LocalDate.now(), "Lunch", "Cafe", 50, "Food"));
        txnDao.save(new Transaction(LocalDate.now(), "Dinner", "Restaurant", 60, "Food"));

        // Create budget for the category
        SetBudgetRequestModel req = new SetBudgetRequestModel();
        req.category = "Food";
        req.limit = 200;
        req.month = YearMonth.now();

        setInteractor.setBudget(req);

        // The interactor should calculate spent = 110 and remaining = 90
        assertEquals("Food", presenter.lastResponse.category);
        assertEquals(200, presenter.lastResponse.limit);
        assertEquals(110, presenter.lastResponse.spent);
        assertEquals(90, presenter.lastResponse.remaining);
        assertEquals("OK", presenter.lastResponse.warningLevel);
    }

    @Test
    void testDeleteBudget() {
        // First create a budget to delete
        SetBudgetRequestModel req = new SetBudgetRequestModel();
        req.category = "Travel";
        req.limit = 500;
        req.month = YearMonth.now();
        setInteractor.setBudget(req);

        // Delete the budget
        deleteInteractor.deleteBudget("Travel");

        // Response should indicate "DELETED"
        assertEquals("Travel", presenter.lastResponse.category);
        assertEquals(0, presenter.lastResponse.limit);
        assertEquals(0, presenter.lastResponse.spent);
        assertEquals("DELETED", presenter.lastResponse.warningLevel);

        // Notification should also be sent
        assertNotNull(presenter.lastNotification);
        assertEquals("Travel", presenter.lastNotification.category);
        assertTrue(presenter.lastNotification.message.contains("deleted"));
    }

    @Test
    void testRefreshBudgetsUpdatesSpentValues() {

        // Create two budgets to refresh
        SetBudgetRequestModel foodReq = new SetBudgetRequestModel();
        foodReq.category = "Food";
        foodReq.limit = 300;
        foodReq.month = YearMonth.now();
        setInteractor.setBudget(foodReq);

        SetBudgetRequestModel funReq = new SetBudgetRequestModel();
        funReq.category = "Fun";
        funReq.limit = 150;
        funReq.month = YearMonth.now();
        setInteractor.setBudget(funReq);

        // Create transactions for both categories
        txnDao.save(new Transaction(LocalDate.now(), "Pizza", "Store", 20, "Food"));
        txnDao.save(new Transaction(LocalDate.now(), "Burger", "FastFood", 30, "Food"));
        txnDao.save(new Transaction(LocalDate.now(), "Movie", "Cinema", 15, "Fun"));

        // Perform refresh — presenter should reflect the last updated budget
        refreshInteractor.refreshAllBudgets();

        assertEquals("Food", presenter.lastResponse.category);
        assertEquals(50, presenter.lastResponse.spent);
        assertEquals(250, presenter.lastResponse.remaining);
        assertEquals("OK", presenter.lastResponse.warningLevel);
    }

    @Test
    void testDeleteBudgetThrowsIfNotExists() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> deleteInteractor.deleteBudget("Nonexistent"));

        assertTrue(ex.getMessage().contains("Budget does not exist"));
    }

    @Test
    void testBudgetSpentCalculator_AllBranchesCovered() {
        InMemoryTransactionDataAccessObject dao = new InMemoryTransactionDataAccessObject();
        BudgetSpentCalculator calc = new BudgetSpentCalculator(dao);

        String target = "Food";
        LocalDate now = LocalDate.now();
        LocalDate lastMonth = now.minusMonths(1);

        // Branch 1: category == null (filtered out)
        dao.save(new Transaction(now, "NullCat", "Store", 100, null));

        // Branch 2: category doesn't match (filtered out)
        dao.save(new Transaction(now, "WrongCat", "Store", 200, "Other"));

        // Branch 3: category matches BUT date is before first day (filtered out)
        dao.save(new Transaction(lastMonth, "OldTx", "Store", 300, target));

        // Branch 4: category matches AND date is valid (included)
        dao.save(new Transaction(now, "ValidTx", "Store", 50, target));

        double result = calc.calculateSpent(target);

        // Only the valid current-month transaction should be counted.
        assertEquals(50, result);
    }
}