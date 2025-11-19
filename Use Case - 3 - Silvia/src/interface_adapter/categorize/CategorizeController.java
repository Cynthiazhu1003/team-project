public class CategorizeController {

    private final CategorizeInputBoundary interactor;

    public CategorizeController(CategorizeInputBoundary interactor) {
        this.interactor = interactor;
    }

    // The UI would call this method when the user changes a category
    public void categorize(int transactionIndex, String newCategory) {
        CategorizeInputData inputData =
                new CategorizeInputData(transactionIndex, newCategory);
        interactor.execute(inputData);
    }
}