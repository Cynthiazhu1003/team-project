package use_case1;

import frontend.Transaction;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

public class UseCase1Test {

    private File createTempCsv(String content) throws IOException {
        File temp = File.createTempFile("transactions_test", ".csv");
        temp.deleteOnExit();
        Files.write(temp.toPath(), content.getBytes());
        return temp;
    }

    @Test
    public void importFromFile_validFile_returnsListOfTransactions() throws IOException {
        String csv =
                "date,description,merchant,amount\n" +
                        "2024-01-01,Groceries,Loblaws,-50.25\n" +
                        "2024-01-02,Coffee,Starbucks,-4.75\n";

        File csvFile = createTempCsv(csv);

        // Use the real gateway
        api.fina.FinaCategorizationGateway gateway = new api.fina.FinaCategorizationGatewayImpl();
        UseCase1 useCase = new UseCase1(gateway);

        List<Transaction> transactions = useCase.importFromFile(csvFile);

        assertEquals(2, transactions.size());

        Transaction t1 = transactions.get(0);
        assertEquals(LocalDate.of(2024, 1, 1), t1.getDate());
        assertEquals("Groceries", t1.getDescription());
        assertEquals("Loblaws", t1.getMerchant());
        assertEquals(-50.25, t1.getAmount(), 0.0001);
        assertNotNull(t1.getCategory()); // category should be set by real gateway

        Transaction t2 = transactions.get(1);
        assertEquals(LocalDate.of(2024, 1, 2), t2.getDate());
        assertEquals("Coffee", t2.getDescription());
        assertEquals("Starbucks", t2.getMerchant());
        assertEquals(-4.75, t2.getAmount(), 0.0001);
        assertNotNull(t2.getCategory()); // category should be set by real gateway
    }

    @Test(expected = IllegalArgumentException.class)
    public void importFromFile_onlyHeader_throwsIllegalArgumentException() throws IOException {
        String csv = "date,description,merchant,amount\n";

        File csvFile = createTempCsv(csv);

        api.fina.FinaCategorizationGateway gateway = new api.fina.FinaCategorizationGatewayImpl();
        UseCase1 useCase = new UseCase1(gateway);

        useCase.importFromFile(csvFile);
    }

    @Test
    public void importFromFile_badFormat_throwsExceptionWithHelpfulMessage() throws IOException {
        String csv =
                "date,description,merchant,amount\n" +
                        "not-a-date,BadRow,Loblaws,-50.25\n";

        File csvFile = createTempCsv(csv);
        api.fina.FinaCategorizationGateway gateway = new api.fina.FinaCategorizationGatewayImpl();
        UseCase1 useCase = new UseCase1(gateway);

        try {
            useCase.importFromFile(csvFile);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().startsWith("Invalid date format"));
        }
    }
}