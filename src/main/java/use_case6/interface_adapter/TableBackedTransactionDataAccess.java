package use_case6.interface_adapter;

import frontend.Transaction;
import use_case2.data_access.TransactionDataAccessInterface;
import java.time.format.DateTimeParseException;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class TableBackedTransactionDataAccess implements TransactionDataAccessInterface {

    private final JTable transactionTable;

    public TableBackedTransactionDataAccess(JTable transactionTable) {
        this.transactionTable = transactionTable;
    }

    @Override
    public void save(Transaction transaction) {
        throw new UnsupportedOperationException("save not used in report use case");
    }

    @Override
    public List<Transaction> getAllTransactions() {
        List<Transaction> result = new ArrayList<>();
        DefaultTableModel model = (DefaultTableModel) transactionTable.getModel();
        int rowCount = model.getRowCount();

        for (int i = 0; i < rowCount; i++) {
            String dateString = model.getValueAt(i, 0).toString();  // "2024-October-15"
            String merchant   = model.getValueAt(i, 1).toString();
            String desc       = model.getValueAt(i, 2).toString();
            double amount     = ((Number) model.getValueAt(i, 3)).doubleValue();
            String category   = model.getValueAt(i, 4).toString();

            LocalDate date = parseUiDate(dateString);
            Transaction t = new Transaction(date, desc, merchant, amount, category);
            result.add(t);
        }

        return result;
    }

    @Override
    public void update(Transaction transaction) {
        throw new UnsupportedOperationException("update not used in report use case");
    }

    @Override
    public void delete(Transaction transaction) {
        throw new UnsupportedOperationException("delete not used in report use case");
    }

    @Override
    public boolean deleteByIndex(int index) {
        return false;
    }

    private LocalDate parseUiDate(String dateString) {
        if (dateString == null || dateString.isBlank()) {
            throw new IllegalArgumentException("Date string is null/blank");
        }

        // 1) Try standard ISO format: "YYYY-MM-DD" (what CSV uses)
        try {
            return LocalDate.parse(dateString);  // uses ISO_LOCAL_DATE
        } catch (DateTimeParseException ignored) {
            // fall through to manual parsing
        }

        // 2) Fallback for formats like "YYYY-January-05" or "YYYY-11-05"
        String[] parts = dateString.split("-");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Unrecognized date format: " + dateString);
        }

        int year = Integer.parseInt(parts[0]);
        String monthPart = parts[1];
        int day = Integer.parseInt(parts[2]);

        Month month;
        try {
            // numeric month like "11"
            int m = Integer.parseInt(monthPart);
            month = Month.of(m); // 1–12
        } catch (NumberFormatException e) {
            // text month like "January"
            month = Month.valueOf(monthPart.toUpperCase());
        }

        return LocalDate.of(year, month, day);
    }
}