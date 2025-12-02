package api.fina;

import java.util.ArrayList;
import java.util.List;

/**
 * Local test class for the Fina Categorization API.
 */
public class LocalTest {
    /**
     * Main for the local test class.
     */
    public static void main(String[] args)
            throws FinaCategorizationGateway.FinaCategorizationException {
        // Build example input rows (Add / delete below to stress test API)
        List<List<String>> rows = new ArrayList<>();

        rows.add(List.of("TESLA SUPERCHARGER US", "Tesla Supercharger", "-211"));
        rows.add(List.of("Starbucks 123", "Starbucks", "-5.90"));
        rows.add(List.of("Walmart Supercenter", "Walmart", "-82.14"));
        rows.add(List.of("AMAZON MKTPLACE PMTS", "Amazon", "-45.22"));
        rows.add(List.of("UBER TRIP", "Uber", "-18.60"));
        rows.add(List.of("LYFT RIDE", "Lyft", "-12.75"));
        rows.add(List.of("MCDONALD'S #3102", "McDonalds", "-7.49"));
        rows.add(List.of("SHELL GAS", "Shell", "-63.40"));
        rows.add(List.of("TARGET T-1234", "Target", "-128.99"));
        rows.add(List.of("APPLE.COM/BILL", "Apple", "-4.99"));
        rows.add(List.of("SPOTIFY", "Spotify", "-9.99"));
        rows.add(List.of("ADOBE CREATIVE CLOUD", "Adobe", "-52.99"));
        rows.add(List.of("NETFLIX.COM", "Netflix", "-15.49"));
        rows.add(List.of("CHEVRON STORE 4089", "Chevron", "-54.33"));
        rows.add(List.of("CVS PHARMACY", "CVS", "-23.80"));
        rows.add(List.of("KROGER MARKETPLACE", "Kroger", "-94.20"));
        rows.add(List.of("AIRBNB * HOST FEE", "Airbnb", "-120.00"));
        rows.add(List.of("MARRIOTT HOTEL", "Marriott", "-289.00"));
        rows.add(List.of("DELTA AIRLINES", "Delta", "-364.10"));
        rows.add(List.of("BEST BUY STORE #550", "Best Buy", "-219.99"));
        rows.add(List.of("HOME DEPOT #8812", "Home Depot", "-74.60"));
        rows.add(List.of("WALGREENS #2291", "Walgreens", "-18.44"));
        rows.add(List.of("CHIPOTLE 1518", "Chipotle", "-11.95"));
        rows.add(List.of("TACO BELL", "Taco Bell", "-6.79"));
        rows.add(List.of("NIKE.COM ORDER", "Nike", "-88.00"));
        rows.add(List.of("COSTCO GAS", "Costco", "-65.10"));
        rows.add(List.of("COSTCO WHOLESALE", "Costco", "-230.44"));
        rows.add(List.of("PAYPAL * EBAY", "eBay", "-27.99"));
        rows.add(List.of("SOUTWEST AIRLINES", "Southwest Airlines", "-412.77"));
        rows.add(List.of("PANDA EXPRESS", "Panda Express", "-9.15"));

        // Create gateway to API
        FinaCategorizationGatewayImpl gateway = new FinaCategorizationGatewayImpl();

        // Call categorize()
        System.out.println("Sending to Fina API...");
        List<List<String>> result = gateway.categorize(rows);

        // Print results
        System.out.println("\n--- Categorized Output ---");
        for (List<String> row : result) {
            System.out.println(row);
        }
    }
}