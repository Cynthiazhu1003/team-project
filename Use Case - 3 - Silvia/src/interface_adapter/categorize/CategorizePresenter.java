public class CategorizePresenter implements CategorizeOutputBoundary {

    private final CategorizeViewModel viewModel;

    public CategorizePresenter(CategorizeViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void presentSuccess(CategorizeOutputData outputData) {
        // Update the total for this category in the ViewModel
        viewModel.setCategoryTotal(
                outputData.getNewCategory(),
                outputData.getNewCategoryTotal()
        );

        // Set a success message for the UI
        viewModel.setMessage("Category updated successfully.");
    }

    @Override
    public void presentFailure(String errorMessage) {
        // Just set the error message
        viewModel.setMessage("Error: " + errorMessage);
    }
}