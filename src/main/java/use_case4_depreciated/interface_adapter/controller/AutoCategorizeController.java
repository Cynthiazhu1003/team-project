package use_case4_depreciated.interface_adapter.controller;

import frontend.Transaction;

import use_case4_depreciated.boundary.AutoCategorizeInputBoundary;
import use_case4_depreciated.data.AutoCategorizeRequestModel;

import java.util.List;

public class AutoCategorizeController {

    private final AutoCategorizeInputBoundary input;

    public AutoCategorizeController(AutoCategorizeInputBoundary input) {
        this.input = input;
    }

    public void categorize(List<Transaction> transactions) {
        AutoCategorizeRequestModel request = new AutoCategorizeRequestModel(transactions);
        input.execute(request);
    }
}