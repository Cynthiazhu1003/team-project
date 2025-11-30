package use_case3.use_case.categorize;

public class CategorizeInputData {
    private final int transactionIndex;
    private final String newCategory;

    public CategorizeInputData(int transactionIndex, String newCategory) {
        this.transactionIndex = transactionIndex;
        this.newCategory = newCategory;
    }

    public int getTransactionIndex() {
        return transactionIndex;
    }

    public String getNewCategory() {
        return newCategory;
    }
}