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

    // ========== Test Presenter ==========
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

    // ========== Setup ==========
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

    // =======================================================
    //    TEST 1 — Set Budget + Computes Spent
    // =======================================================
    @Test
    void testSetBudgetWithExistingTransactions() {

        // Fake transactions
        txnDao.save(new Transaction(LocalDate.now(), "Lunch", "Cafe", 50, "Food"));
        txnDao.save(new Transaction(LocalDate.now(), "Dinner", "Restaurant", 60, "Food"));

        SetBudgetRequestModel req = new SetBudgetRequestModel();
        req.category = "Food";
        req.limit = 200;
        req.month = YearMonth.now();

        setInteractor.setBudget(req);

        assertEquals("Food", presenter.lastResponse.category);
        assertEquals(200, presenter.lastResponse.limit);
        assertEquals(110, presenter.lastResponse.spent);
        assertEquals(90, presenter.lastResponse.remaining);
        assertEquals("OK", presenter.lastResponse.warningLevel);
    }

    // =======================================================
    //    TEST 2 — Delete Budget
    // =======================================================
    @Test
    void testDeleteBudget() {
        // Create a budget first
        SetBudgetRequestModel req = new SetBudgetRequestModel();
        req.category = "Travel";
        req.limit = 500;
        req.month = YearMonth.now();
        setInteractor.setBudget(req);

        // Now delete it
        deleteInteractor.deleteBudget("Travel");

        assertEquals("Travel", presenter.lastResponse.category);
        assertEquals(0, presenter.lastResponse.limit);
        assertEquals(0, presenter.lastResponse.spent);
        assertEquals("DELETED", presenter.lastResponse.warningLevel);

        assertNotNull(presenter.lastNotification);
        assertEquals("Travel", presenter.lastNotification.category);
        assertTrue(presenter.lastNotification.message.contains("deleted"));
    }

    // =======================================================
    //    TEST 3 — Refresh All Budgets (Recomputes Spending)
    // =======================================================
    @Test
    void testRefreshBudgetsUpdatesSpentValues() {

        // Create budgets
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

        // Fake transactions
        txnDao.save(new Transaction(LocalDate.now(), "Pizza", "Store", 20, "Food"));
        txnDao.save(new Transaction(LocalDate.now(), "Burger", "FastFood", 30, "Food"));
        txnDao.save(new Transaction(LocalDate.now(), "Movie", "Cinema", 15, "Fun"));

        // Refresh all budgets
        refreshInteractor.refreshAllBudgets();

        // Last updated budget will be "Food" due to iteration order
        assertEquals("Food", presenter.lastResponse.category);
        assertEquals(50, presenter.lastResponse.spent);
        assertEquals(250, presenter.lastResponse.remaining);
        assertEquals("OK", presenter.lastResponse.warningLevel);
    }
}