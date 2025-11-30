package use_case5;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case5.boundary.*;
import use_case5.data.InMemoryBudgetRepository;
import use_case5.use_case.BudgetInteractor;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BudgetInteractorTest {

    private InMemoryBudgetRepository repo;
    private TestPresenter presenter;
    private BudgetInteractor interactor;

    // A fake presenter to capture outputs
    static class TestPresenter implements BudgetOutputBoundary {

        final List<BudgetResponseModel> responses = new ArrayList<>();
        final List<BudgetNotificationModel> notifications = new ArrayList<>();

        @Override
        public void presentBudget(BudgetResponseModel responseModel) {
            responses.add(responseModel);
        }

        @Override
        public void presentNotification(BudgetNotificationModel notificationModel) {
            notifications.add(notificationModel);
        }
    }

    @BeforeEach
    void setup() {
        repo = new InMemoryBudgetRepository();
        presenter = new TestPresenter();
        interactor = new BudgetInteractor(repo, presenter);
    }

    @Test
    void testSetBudget() {
        SetBudgetRequestModel req = new SetBudgetRequestModel();
        req.category = "Food";
        req.limit = 500;
        req.month = YearMonth.now();

        interactor.setBudget(req);

        assertEquals(1, presenter.responses.size());
        BudgetResponseModel res = presenter.responses.get(0);

        assertEquals("Food", res.category);
        assertEquals(500, res.limit);
        assertEquals(0, res.spent);
        assertEquals(500, res.remaining);
        assertEquals("OK", res.warningLevel);

        assertNotNull(repo.find("Food"));
    }

    @Test
    void testAddSpendingTriggersWarning() {
        // Set a budget
        SetBudgetRequestModel req = new SetBudgetRequestModel();
        req.category = "Transport";
        req.limit = 100;
        req.month = YearMonth.now();
        interactor.setBudget(req);

        // Add spending of 85 → 85% → WARNING
        UpdateBudgetRequestModel up = new UpdateBudgetRequestModel();
        up.category = "Transport";
        up.amount = 85;
        interactor.addSpending(up);

        // Get the last response
        BudgetResponseModel res = presenter.responses.get(presenter.responses.size() - 1);

        assertEquals(85, res.spent);
        assertEquals("WARNING", res.warningLevel);

        BudgetNotificationModel notif = presenter.notifications.get(presenter.notifications.size() - 1);
        assertEquals("Transport", notif.category);
        assertEquals("Budget WARNING", notif.message);
    }

    @Test
    void testExceededBudget() {
        // Set budget
        SetBudgetRequestModel req = new SetBudgetRequestModel();
        req.category = "Games";
        req.limit = 50;
        req.month = YearMonth.now();
        interactor.setBudget(req);

        // Add spending of 60 → exceed
        UpdateBudgetRequestModel up = new UpdateBudgetRequestModel();
        up.category = "Games";
        up.amount = 60;
        interactor.addSpending(up);

        BudgetResponseModel res = presenter.responses.get(presenter.responses.size() - 1);

        assertEquals(60, res.spent);
        assertEquals("EXCEEDED", res.warningLevel);

        BudgetNotificationModel notif = presenter.notifications.get(presenter.notifications.size() - 1);
        assertEquals("Budget EXCEEDED", notif.message);
    }

    @Test
    void testAddSpendingToNonexistentBudgetThrows() {
        UpdateBudgetRequestModel up = new UpdateBudgetRequestModel();
        up.category = "Random";
        up.amount = 20;

        assertThrows(RuntimeException.class, () -> interactor.addSpending(up));
    }
}