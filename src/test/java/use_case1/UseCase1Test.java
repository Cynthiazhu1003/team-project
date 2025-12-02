package use_case1;

import api.fina.FinaCategorizationGateway;
import api.fina.FinaCategorizationGatewayImpl;
import frontend.Transaction;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UseCase1Test {

    private File createTempCsv(String content) throws IOException {
        File file = File.createTempFile("testcsv", ".csv");
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(content);
        }
        return file;
    }

    static class ThrowingGateway implements FinaCategorizationGateway {
        @Override
        public List<List<String>> categorize(List<List<String>> rows) throws FinaCategorizationException {
            throw new FinaCategorizationException("forced");
        }
    }

    @Test
    void importFromFile_emptyFile_throws() throws IOException {
        File file = createTempCsv("");
        UseCase1 useCase = new UseCase1(new FinaCategorizationGatewayImpl());
        assertThrows(RuntimeException.class, () -> useCase.importFromFile(file));
    }

    @Test
    void importFromFile_badHeader_throws() throws IOException {
        String csv = "wrong,header,fields\n1,2,3,4\n";
        File file = createTempCsv(csv);
        UseCase1 useCase = new UseCase1(new FinaCategorizationGatewayImpl());
        assertThrows(RuntimeException.class, () -> useCase.importFromFile(file));
    }

    @Test
    void importFromFile_invalidDate_throws() throws IOException {
        String csv =
                "date,description,merchant,amount\n" +
                        "not-a-date,Test,Loblaws,-10.0\n";
        File file = createTempCsv(csv);
        UseCase1 useCase = new UseCase1(new FinaCategorizationGatewayImpl());
        assertThrows(IllegalArgumentException.class, () -> useCase.importFromFile(file));
    }

    @Test
    void importFromFile_invalidAmount_throws() throws IOException {
        String csv =
                "date,description,merchant,amount\n" +
                        "2024-01-01,Test,Loblaws,not-a-number\n";
        File file = createTempCsv(csv);
        UseCase1 useCase = new UseCase1(new FinaCategorizationGatewayImpl());
        assertThrows(IllegalArgumentException.class, () -> useCase.importFromFile(file));
    }

    @Test
    void importFromFile_headerOnly_throws() throws IOException {
        String csv = "date,description,merchant,amount\n";
        File file = createTempCsv(csv);
        UseCase1 useCase = new UseCase1(new FinaCategorizationGatewayImpl());
        assertThrows(IllegalArgumentException.class, () -> useCase.importFromFile(file));
    }

    @Test
    void importFromFile_validRow_parsesCorrectly() throws IOException {
        String csv =
                "date,description,merchant,amount\n" +
                        "2024-01-01,Groceries,Loblaws,-50.25\n";
        File file = createTempCsv(csv);
        UseCase1 useCase = new UseCase1(new FinaCategorizationGatewayImpl());
        List<Transaction> result = useCase.importFromFile(file);
        assertEquals(1, result.size());
        Transaction t = result.get(0);
        assertEquals(LocalDate.of(2024, 1, 1), t.getDate());
        assertEquals("Groceries", t.getDescription());
        assertEquals("Loblaws", t.getMerchant());
        assertEquals(-50.25, t.getAmount(), 0.0001);
        assertNotNull(t.getCategory());
    }

    @Test
    void importFromFile_wrongNumberOfColumns_throws() throws IOException {
        String csv =
                "date,description,merchant,amount\n" +
                        "2024-01-01,OnlyTwoColumns\n";
        File file = createTempCsv(csv);
        UseCase1 useCase = new UseCase1(new FinaCategorizationGatewayImpl());
        assertThrows(IllegalArgumentException.class, () -> useCase.importFromFile(file));
    }

    @Test
    void importFromFile_gatewayThrows_wrapsException() throws IOException {
        String csv =
                "date,description,merchant,amount\n" +
                        "2024-01-01,Groceries,Loblaws,-50.25\n";
        File file = createTempCsv(csv);
        UseCase1 useCase = new UseCase1(new ThrowingGateway());
        assertThrows(RuntimeException.class, () -> useCase.importFromFile(file));
    }
}