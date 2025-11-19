public class CategorizeOutputData {
    private final String newCategory;
    private final double newCategoryTotal;

    public CategorizeOutputData(String newCategory, double newCategoryTotal) {
        this.newCategory = newCategory;
        this.newCategoryTotal = newCategoryTotal;
    }

    public String getNewCategory() {
        return newCategory;
    }

    public double getNewCategoryTotal() {
        return newCategoryTotal;
    }
}