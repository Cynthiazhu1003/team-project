package frontend;

import java.time.LocalDate;

public class Transaction {
    private LocalDate date;
    private String description;
    private String merchant;
    private double amount;

    public Transaction(LocalDate date, String description, String merchant, double amount){
        this.date = date;
        this.description = description;
        this.merchant = merchant;
        this.amount = amount;
    }
    public static Transaction of(String line){

        String[] parts = line.split(",");
        LocalDate date = LocalDate.parse(parts[0]);
        String description = parts[1];
        String merchant = parts[2];
        double amount = Double.parseDouble(parts[3]);



        return new Transaction( date, description, merchant, amount);
    }
    @Override
    public String toString(){
        return String.format("%s %-20s %-20s %8.2f", date, description, merchant, amount);
    }


}
