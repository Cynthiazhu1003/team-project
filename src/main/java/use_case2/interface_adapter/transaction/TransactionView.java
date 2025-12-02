package use_case2.interface_adapter.transaction;


import use_case2.interface_adapter.add_transaction.AddTransactionController;
import use_case2.interface_adapter.edit_transaction.EditTransactionController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;

public class TransactionView extends JPanel implements ActionListener, PropertyChangeListener {
    public final String viewName = "transaction";
    private final TransactionViewModel transactionViewModel;
    private final AddTransactionController addTransactionController;
    private final EditTransactionController editTransactionController;

    // UI Components
    private final JLabel titleLabel = new JLabel(TransactionViewModel.TITLE_LABEL);
    private final JButton addTransactionButton = new JButton(TransactionViewModel.ADD_BUTTON_LABEL);

    // Form components
    private final JTextField dateField = new JTextField(10);
    private final JTextField categoryField = new JTextField(10);
    private final JTextField  merchantField = new JTextField(10);
    private final JTextField amountField = new JTextField(10);
    private final JTextField descriptionField = new JTextField(10);
    private final JButton submitButton = new JButton("Submit");

    private final JLabel errorLabel = new JLabel();
    private final JLabel successLabel = new JLabel();

    public TransactionView(TransactionViewModel transactionViewModel,
                           AddTransactionController addTransactionController, EditTransactionController
                                   editTransactionController) {
        this.transactionViewModel = transactionViewModel;
        this.addTransactionController = addTransactionController;
        this.editTransactionController = editTransactionController;
        this.transactionViewModel.addPropertyChangeListener(this);


        setupUI();
        layoutComponents();
    }

    private void setupUI() {
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        errorLabel.setForeground(Color.RED);
        successLabel.setForeground(Color.GREEN);

        addTransactionButton.addActionListener(this);
        submitButton.addActionListener(this);

        // Set today's date as default
        dateField.setText(LocalDate.now().toString());
    }

    private void layoutComponents() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Title
        this.add(Box.createVerticalStrut(20));
        this.add(titleLabel);
        this.add(Box.createVerticalStrut(30));

        // Add Transaction Button
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addTransactionButton);
        this.add(buttonPanel);
        this.add(Box.createVerticalStrut(20));

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        formPanel.add(dateField);

        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryField);

        formPanel.add(new JLabel("Merchant:"));
        formPanel.add(merchantField);

        formPanel.add(new JLabel("Amount:"));
        formPanel.add(amountField);

        formPanel.add(new JLabel("Description:"));
        formPanel.add(descriptionField);

        formPanel.add(new JLabel("")); // Empty cell
        formPanel.add(submitButton);

        this.add(formPanel);
        this.add(Box.createVerticalStrut(10));

        // Messages
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.add(errorLabel);
        messagePanel.add(successLabel);
        this.add(messagePanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            handleAddTransaction();
        }
    }

    private void handleAddTransaction() {
        try {
            LocalDate date = LocalDate.parse(dateField.getText());
            String category = categoryField.getText();
            String merchant = merchantField.getText();
            double amount = Double.parseDouble(amountField.getText());
            String description = descriptionField.getText();

            TransactionState state = transactionViewModel.getState();
            // Check if we're in edit mode or add mode
            if (state.getEditingTransactionIndex() >= 0 && editTransactionController != null) {
                // Edit mode - use EditTransactionController
                editTransactionController.execute(
                        state.getEditingTransactionIndex(),
                        date, description, merchant, amount, category
                );
            } else {
                // Add mode - use AddTransactionController
                addTransactionController.execute(date, description, merchant, amount, category);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        TransactionState state = transactionViewModel.getState();

        // Update error/success messages
        if (state.getTransactionError() != null) {
            errorLabel.setText(state.getTransactionError());
            successLabel.setText("");
        } else if (state.getTransactionSuccess() != null) {
            successLabel.setText(state.getTransactionSuccess());
            errorLabel.setText("");

            // Clear form on success
            if (state.getTransactionSuccess().contains("successfully")) {
                clearForm();
                if (state.getEditingTransactionIndex() >= 0) {
                    state.setEditingTransactionIndex(-1);
                    transactionViewModel.setState(state);
                    submitButton.setText("Submit"); // Reset button text if changed
                }
            }
        }
        // Handle entering edit mode (if you want to support pre-populating form)
        if (state.getEditingTransactionIndex() >= 0) {
            // If you want to change UI to indicate edit mode, do it here
            submitButton.setText("Update Transaction");
        }
    }
    private void clearForm() {
        dateField.setText(LocalDate.now().toString());
        categoryField.setText("");
        merchantField.setText("");
        amountField.setText("");
        descriptionField.setText("");
    }

}


