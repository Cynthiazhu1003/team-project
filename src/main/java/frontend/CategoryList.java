package frontend;

import java.util.ArrayList;
import java.util.List;

public final class CategoryList {

    // prevent instantiation
    private CategoryList() {}

    // Base categories shared by everything
    private static final String[] BASE_CATEGORIES = {
        "home",
        "bills & utilities","auto & transport","vehicle & repairs","gas","other transportation",
        "food & drink","groceries","restaurants & other","health & wellness","medical","gym",
        "other health & wellness","travel & vacation","shopping","clothing","other shopping",
        "entertainment & lifestyle","education","gifts & donations","loans & financial fees",
        "family & pets","subscriptions","business & work","investments","taxes","insurance",
        "other expenses","primary paycheck","business income","repayment from others",
        "other income","transfer","credit card payment"
    };

    /** For dropdowns like the *form* (Add/Edit Transaction/Budget). */
    public static List<String> getFormCategories() {
        List<String> result = new ArrayList<>();
        result.add("Select...");
        for (String c : BASE_CATEGORIES) {
            result.add(c);
        }
        return result;
    }

    /** For filter dropdown (Transactions filter) – starts with "All Categories". */
    public static List<String> getFilterCategories() {
        List<String> result = new ArrayList<>();
        result.add("All Categories");
        for (String c : BASE_CATEGORIES) {
            result.add(c);
        }
        return result;
    }

    /** Optional: if you ever want just the raw list. */
    public static List<String> getBaseCategories() {
        List<String> result = new ArrayList<>();
        for (String c : BASE_CATEGORIES) {
            result.add(c);
        }
        return result;
    }
}
