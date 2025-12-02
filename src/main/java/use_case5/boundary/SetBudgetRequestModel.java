package use_case5.boundary;

import java.time.YearMonth;

/**
 * Set budget request model for the set budget use case.
 */
public class SetBudgetRequestModel {
    public String category;
    public double limit;
    public YearMonth month;
}