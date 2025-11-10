package use_case.add_transaction;

import java.time.LocalDate;

public class AddTransactionInputData {
    private final LocalDate date;
    private final double amount;
    private final String category;
    private final String description;

    public AddTransactionInputData( LocalDate date,
                                   double amount, String category, String description) {
        this.date = date;
        this.amount = amount;
        this.category = category;
        this.description = description;
    }

    public LocalDate getDate() { return date; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
}
