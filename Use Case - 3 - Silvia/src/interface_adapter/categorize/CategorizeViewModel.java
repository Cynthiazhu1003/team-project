import java.util.HashMap;
import java.util.Map;

public class CategorizeViewModel {

    // category name -> total amount in that category
    private Map<String, Double> categoryTotals = new HashMap<>();

    // a status or error message for the UI to display
    private String message = "";

    public Map<String, Double> getCategoryTotals() {
        return categoryTotals;
    }

    public void setCategoryTotal(String category, double total) {
        categoryTotals.put(category, total);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}