package use_case5;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case5.boundary.*;
import use_case5.data.InMemoryBudgetRepository;
import use_case5.use_case.BudgetInteractor;
import frontend.Transaction;
import use_case2.data_access.InMemoryTransactionDataAccessObject;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

class BudgetWithTransactionsTest {

    private InMemoryTransactionDataAccessObject txnDao;
    private TestPresenter presenter;
    private BudgetInteractor interactor;

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

    @BeforeEach
    void setup() {
        InMemoryBudgetRepository budgetRepo = new InMemoryBudgetRepository();
        txnDao = new InMemoryTransactionDataAccessObject();
        presenter = new TestPresenter();
        interactor = new BudgetInteractor(budgetRepo, txnDao, presenter);
    }

    @Test
    void testBudgetWithFakeTransactions() {

        // Set a budget
        SetBudgetRequestModel setReq = new SetBudgetRequestModel();
        setReq.category = "Food";
        setReq.limit = 200;
        setReq.month = YearMonth.now();
        interactor.setBudget(setReq);

        // Add fake transactions to DAO
        txnDao.save(new Transaction(LocalDate.now(), "Lunch", "Cafe", 50, "Food"));
        txnDao.save(new Transaction(LocalDate.now(), "Dinner", "Restaurant", 60, "Food"));
        txnDao.save(new Transaction(LocalDate.now(), "Snack", "Store", 20, "Food"));

        // Trigger spending update (amount ignored)
        UpdateBudgetRequestModel updateReq = new UpdateBudgetRequestModel();
        updateReq.category = "Food";
        interactor.addSpending(updateReq);

        // Assertions
        assertEquals(130, presenter.lastResponse.spent);
        assertEquals(70, presenter.lastResponse.remaining);
        assertEquals("OK", presenter.lastResponse.warningLevel);
    }
}