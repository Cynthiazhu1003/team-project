package use_case1;

import frontend.Transaction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

public class UseCase1 {

    public static List<Transaction> importFromFile(File selectedFile) throws IOException {

        List<String> data = Files.readAllLines(selectedFile.toPath());

        if (data.size() <= 1) {
            throw new IllegalArgumentException("File has no data rows.");
        }

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

        return data.stream()
                .skip(1)
                .map(Transaction::of)
                .collect(Collectors.toList());
    }
}
