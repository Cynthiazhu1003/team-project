package use_case3.interface_adapter.categorize;

import use_case3.use_case.categorize.CategorizeInputBoundary;
import use_case3.use_case.categorize.CategorizeInputData;

public class CategorizeController {

    private final CategorizeInputBoundary interactor;

    public CategorizeController(CategorizeInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void categorize(int transactionIndex, String newCategory) {
        CategorizeInputData inputData =
                new CategorizeInputData(transactionIndex, newCategory);
        interactor.execute(inputData);
    }
}