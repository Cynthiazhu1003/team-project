package use_case2.use_case_addTrans;

import java.time.LocalDate;

public class AddTransactionInputData {
    private final LocalDate date;
    private final String description;
    private final String merchant;
    private final double amount;
    private final String category;


    public AddTransactionInputData( LocalDate date,
                                    String description, String merchant, double amount, String category) {
        this.date = date;
        this.description = description;
        this.merchant = merchant;
        this.amount = amount;
        this.category = category;

    }

    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
    public String getMerchant() { return merchant; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }

}
