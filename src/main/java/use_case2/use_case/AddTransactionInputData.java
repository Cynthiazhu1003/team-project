package use_case2.use_case;

import java.time.LocalDate;

public class AddTransactionInputData {
    private final LocalDate date;
    private final String description;
    private final double amount;
    private final String category;


    public AddTransactionInputData( LocalDate date,
                                    String description, double amount, String category) {
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.category = category;

    }

    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }

}
