package use_case1;

import api.fina.FinaCategorizationGateway;
import api.fina.FinaCategorizationGateway.FinaCategorizationException;
import frontend.Transaction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UseCase1 {

    private final FinaCategorizationGateway gateway;

    public UseCase1(FinaCategorizationGateway gateway) {
        this.gateway = gateway;
    }

    public List<Transaction> importFromFile(File selectedFile) throws IOException {
        // Read CSV
        List<String> data = Files.readAllLines(selectedFile.toPath());
        if (data.size() <= 1) {
            throw new IllegalArgumentException("File has no data rows.");
        }

        // Validate format
        boolean isFormatCorrect = data.stream()
                .skip(1)
                .allMatch(s -> s.matches(
                        "\\d{4}-\\d{2}-\\d{2}\\s*,\\s*[^,]+\\s*,\\s*[^,]+\\s*,-?[0-9]+(\\.[0-9]+)?"
                ));
        if (!isFormatCorrect) {
            throw new IllegalArgumentException(
                    "CSV format is not correct.\n" +
                            "Expected: date, description, merchant, -amount.decimals"
            );
        }

        // Map CSV rows → Transaction objects
        List<Transaction> transactions = data.stream()
                .skip(1)
                .map(Transaction::of)
                .collect(Collectors.toList());

        // Convert transactions → API rows
        List<List<String>> rowsForApi = new ArrayList<>();
        for (Transaction t : transactions) {
            rowsForApi.add(List.of(
                    t.getDescription(),
                    t.getMerchant(),
                    Double.toString(t.getAmount())
            ));
        }

        // Call gateway to categorize
        List<List<String>> categorizedRows;
        try {
            categorizedRows = gateway.categorize(rowsForApi);
        } catch (FinaCategorizationException e) {
            throw new RuntimeException("Failed to categorize transactions: " + e.getMessage(), e);
        }

        // Step 6: Attach categories back to transactions
        for (int i = 0; i < categorizedRows.size(); i++) {
            String category = categorizedRows.get(i).get(3); // category is last
            transactions.get(i).setCategory(category);
        }

        return transactions;
    }
}