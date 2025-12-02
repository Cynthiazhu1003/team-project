package use_case1;

import api.fina.FinaCategorizationGateway;
import api.fina.FinaCategorizationGateway.FinaCategorizationException;
import frontend.Transaction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class UseCase1 {

    private final FinaCategorizationGateway gateway;

    public UseCase1(FinaCategorizationGateway gateway) {
        this.gateway = gateway;
    }

    public List<Transaction> importFromFile(File selectedFile) throws IOException {
        List<String> data = Files.readAllLines(selectedFile.toPath());
        if (data.size() <= 1) {
            throw new IllegalArgumentException("File has no data rows.");
        }

        // Define multiple formatters to try
        DateTimeFormatter[] formatters = new DateTimeFormatter[]{
                DateTimeFormatter.ofPattern("yyyy-M-d"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("yyyy-MMM-d", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("yyyy-MMM-dd", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("yyyy-MMMM-d", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("yyyy-MMMM-dd", Locale.ENGLISH)
        };

        List<Transaction> transactions = new ArrayList<>();

        // Parse CSV rows
        for (String line : data.subList(1, data.size())) {
            String[] parts = line.split(",", -1);
            if (parts.length != 4) {
                throw new IllegalArgumentException("CSV row must have 4 columns: " + line);
            }

            String dateStr = parts[0].trim();
            String description = parts[1].trim();
            String merchant = parts[2].trim();
            double amount;

            try {
                amount = Double.parseDouble(parts[3].trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid amount in row: " + line);
            }

            // Parse date using multiple formatters
            LocalDate date = null;
            for (DateTimeFormatter fmt : formatters) {
                try {
                    date = LocalDate.parse(dateStr, fmt);
                    break;
                } catch (Exception ignored) { }
            }
            if (date == null) {
                throw new IllegalArgumentException("Invalid date format in row: " + line);
            }

            transactions.add(new Transaction(date, description, merchant, amount));
        }

        // Prepare rows for Fina gateway
        List<List<String>> rowsForApi = transactions.stream()
                .map(t -> List.of(t.getDescription(), t.getMerchant(), Double.toString(t.getAmount())))
                .collect(Collectors.toList());

        // Call Fina gateway
        List<List<String>> categorizedRows;
        try {
            categorizedRows = gateway.categorize(rowsForApi);
        } catch (FinaCategorizationGateway.FinaCategorizationException e) {
            throw new RuntimeException("Failed to categorize transactions: " + e.getMessage(), e);
        }

        // Attach categories back to transactions
        for (int i = 0; i < categorizedRows.size(); i++) {
            String category = categorizedRows.get(i).get(3); // category is last
            transactions.get(i).setCategory(category);
        }

        return transactions;
    }
}