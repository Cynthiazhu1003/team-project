package use_case2.interface_adapter.transaction_Managment;

public class TransactionViewModel extends ViewModel<TransactionState> {
    public static final String TITLE_LABEL = "Transactions";
    public static final String ADD_BUTTON_LABEL = "Add Transaction";

    public TransactionViewModel() {
        super("transaction");
        setState(new TransactionState());
    }
}