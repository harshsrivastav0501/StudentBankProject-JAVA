package bank.gui;

import bank.exception.AccountNotFoundException;
import bank.exception.InsufficientFundsException;
import bank.model.Account;
import bank.model.CurrentAccount;
import bank.model.SavingsAccount;
import bank.service.Bank;
import bank.service.FileStorage;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/** Swing interface for opening accounts and carrying out basic banking operations. */
public class BankFrame extends JFrame {
    private final Bank bank;
    private final FileStorage storage;
    private final JTextField accountField = new JTextField();
    private final JTextField amountField = new JTextField();
    private final JLabel statusLabel = new JLabel("Welcome to Student Bank");
    private final JTextArea accountList = new JTextArea(10, 42);

    public BankFrame(Bank bank, FileStorage storage) {
        super("Student Bank Management System");
        this.bank = bank;
        this.storage = storage;

        JPanel fields = new JPanel(new GridLayout(2, 2, 8, 8));
        fields.setBorder(BorderFactory.createTitledBorder("Transaction details"));
        fields.add(new JLabel("Account number:"));
        fields.add(accountField);
        fields.add(new JLabel("Amount:"));
        fields.add(amountField);

        JPanel buttons = new JPanel(new GridLayout(2, 3, 8, 8));
        buttons.add(makeButton("Open Account", this::openAccount));
        buttons.add(makeButton("Deposit", () -> transact("Deposit")));
        buttons.add(makeButton("Withdraw", () -> transact("Withdraw")));
        buttons.add(makeButton("Check Balance", this::checkBalance));
        buttons.add(makeButton("Refresh Accounts", this::refreshAccounts));
        buttons.add(makeButton("Save Data", this::saveData));

        accountList.setEditable(false);
        accountList.setFont(new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, 12));
        JPanel center = new JPanel(new BorderLayout(8, 8));
        center.add(buttons, BorderLayout.NORTH);
        center.add(new JScrollPane(accountList), BorderLayout.CENTER);

        setLayout(new BorderLayout(10, 10));
        JPanel north = new JPanel(new BorderLayout(8, 8));
        north.add(fields, BorderLayout.CENTER);
        add(north, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        add(statusLabel, BorderLayout.SOUTH);

        refreshAccounts();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 430);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JButton makeButton(String label, Runnable action) {
        JButton button = new JButton(label);
        button.addActionListener(event -> action.run());
        return button;
    }

    private void openAccount() {
        JTextField nameField = new JTextField();
        JComboBox<String> typeField = new JComboBox<>(new String[]{"Savings", "Current"});
        JTextField depositField = new JTextField("0");
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Account holder name:")); form.add(nameField);
        form.add(new JLabel("Account type:")); form.add(typeField);
        form.add(new JLabel("Initial deposit:")); form.add(depositField);

        int result = JOptionPane.showConfirmDialog(this, form, "Open New Account",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            String name = nameField.getText().trim();
            if (name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty.");
            double deposit = Double.parseDouble(depositField.getText().trim());
            if (!Double.isFinite(deposit) || deposit < 0)
                throw new IllegalArgumentException("Initial deposit must be zero or more.");

            Account account = typeField.getSelectedIndex() == 0
                    ? new SavingsAccount(name, deposit)
                    : new CurrentAccount(name, deposit);
            bank.addAccount(account);
            statusLabel.setText("Account opened: " + account.getAccountNo() + " for " + name);
            refreshAccounts();
        } catch (NumberFormatException ex) {
            showError("Enter a valid initial deposit.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void transact(String action) {
        try {
            Account account = getSelectedAccount();
            double amount = Double.parseDouble(amountField.getText().trim());
            if (!Double.isFinite(amount) || amount <= 0)
                throw new IllegalArgumentException("Amount must be a positive number.");
            if (action.equals("Deposit")) account.deposit(amount);
            else account.withdraw(amount);
            statusLabel.setText(action + " successful. Balance: " + money(account.getBalance()));
            refreshAccounts();
        } catch (NumberFormatException ex) {
            showError("Enter a valid account number and amount.");
        } catch (AccountNotFoundException | IllegalArgumentException ex) {
            showError(ex.getMessage());
        } catch (InsufficientFundsException ex) {
            showError(ex.getMessage() + " (short by " + money(ex.getShortfall()) + ")");
        }
    }

    private void checkBalance() {
        try {
            Account account = getSelectedAccount();
            statusLabel.setText("Balance for " + account.getHolderName() + " (#"
                    + account.getAccountNo() + "): " + money(account.getBalance()));
        } catch (NumberFormatException ex) {
            showError("Enter a valid account number.");
        } catch (AccountNotFoundException ex) {
            showError(ex.getMessage());
        }
    }

    private Account getSelectedAccount() {
        String value = accountField.getText().trim();
        if (value.isEmpty()) throw new IllegalArgumentException("Enter an account number.");
        return bank.findAccount(Integer.parseInt(value));
    }

    private void refreshAccounts() {
        accountList.setText(bank.statement());
    }

    private void saveData() {
        try {
            storage.save(bank.getAccounts());
            statusLabel.setText("All account data saved successfully.");
        } catch (IOException ex) {
            showError("Could not save data: " + ex.getMessage());
        }
    }

    private void showError(String message) {
        statusLabel.setText(message);
        JOptionPane.showMessageDialog(this, message, "Student Bank", JOptionPane.ERROR_MESSAGE);
    }

    private String money(double amount) {
        return String.format("%.2f", amount);
    }
}
