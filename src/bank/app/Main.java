package bank.app;

import bank.exception.AccountNotFoundException;
import bank.exception.InsufficientFundsException;
import bank.gui.BankFrame;
import bank.model.Account;
import bank.model.CurrentAccount;
import bank.model.SavingsAccount;
import bank.service.Bank;
import bank.service.FileStorage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.SwingUtilities;

/** Entry point for the Student Bank console and Swing application. */
public class Main {
    private static final String DATA_FILE = "bank-data.ser";

    public static void main(String[] args) {
        final Bank bank = new Bank();
        final FileStorage storage = new FileStorage(DATA_FILE);

        try {
            bank.loadAccounts(storage.load());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Could not load saved accounts: " + e.getMessage());
            System.out.println("Starting with an empty bank.");
        }

        if (bank.size() == 0) {
            seedDemoData(bank);
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("gui")) {
            SwingUtilities.invokeLater(() -> new BankFrame(bank, storage));
        } else {
            runConsole(bank, storage);
        }
    }

    private static void seedDemoData(Bank bank) {
        bank.addAccount(new SavingsAccount("Asha", 2000));
        bank.addAccount(new CurrentAccount("Ravi", 500));
        System.out.println("Sample accounts created for demonstration.");
    }

    private static void runConsole(Bank bank, FileStorage storage) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            boolean running = true;
            while (running) {
                printMenu();
                String choice = in.readLine();
                if (choice == null) break;
                try {
                    switch (choice.trim()) {
                        case "1": System.out.print(bank.statement()); break;
                        case "2": openAccount(bank, in); break;
                        case "3": deposit(bank, in); break;
                        case "4": withdraw(bank, in); break;
                        case "5": transfer(bank, in); break;
                        case "6": searchAccount(bank, in); break;
                        case "7": System.out.printf("Total balance: %.2f%n", bank.totalDeposits()); break;
                        case "8": save(bank, storage); break;
                        case "0": save(bank, storage); running = false; break;
                        default: System.out.println("Please choose a valid menu option.");
                    }
                } catch (InsufficientFundsException e) {
                    System.out.println("Transaction failed: " + e.getMessage()
                            + " (short by " + money(e.getShortfall()) + ")");
                } catch (AccountNotFoundException | IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                } catch (IOException e) {
                    System.out.println("File error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Input error: " + e.getMessage());
        }
        System.out.println("Thank you for using Student Bank.");
    }

    private static void printMenu() {
        System.out.println("\n========== " + Account.getBankName() + " ==========");
        System.out.println("1. Show all accounts");
        System.out.println("2. Open a new account");
        System.out.println("3. Deposit money");
        System.out.println("4. Withdraw money");
        System.out.println("5. Transfer money");
        System.out.println("6. Search account by number or holder name");
        System.out.println("7. Show total balance in bank");
        System.out.println("8. Save data");
        System.out.println("0. Save and exit");
        System.out.print("Choose an option: ");
    }

    private static void openAccount(Bank bank, BufferedReader in) throws IOException {
        System.out.print("Account holder name: ");
        String name = in.readLine();
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }
        System.out.println("1. Savings account (no overdraft)");
        System.out.println("2. Current account (overdraft facility)");
        int type = readInt(in, "Choose account type: ");
        double initialDeposit = readDouble(in, "Initial deposit (0 or more): ");
        if (initialDeposit < 0) {
            System.out.println("Initial deposit cannot be negative.");
            return;
        }

        Account account;
        if (type == 1) {
            account = new SavingsAccount(name.trim(), initialDeposit);
        } else if (type == 2) {
            account = new CurrentAccount(name.trim(), initialDeposit);
        } else {
            System.out.println("Invalid account type.");
            return;
        }
        bank.addAccount(account);
        System.out.println("Account opened successfully.");
        System.out.println("Account number: " + account.getAccountNo());
        System.out.println("Account holder: " + account.getHolderName());
        System.out.println("Type: " + account.accountType());
        System.out.println("Opening balance: " + money(account.getBalance()));
    }

    private static void deposit(Bank bank, BufferedReader in) throws IOException {
        Account account = bank.findAccount(readInt(in, "Account number: "));
        account.deposit(readDouble(in, "Amount to deposit: "));
        System.out.println("Deposit successful. New balance: " + money(account.getBalance()));
    }

    private static void withdraw(Bank bank, BufferedReader in)
            throws IOException, InsufficientFundsException {
        Account account = bank.findAccount(readInt(in, "Account number: "));
        account.withdraw(readDouble(in, "Amount to withdraw: "));
        System.out.println("Withdrawal successful. New balance: " + money(account.getBalance()));
    }

    private static void transfer(Bank bank, BufferedReader in)
            throws IOException, InsufficientFundsException {
        Account from = bank.findAccount(readInt(in, "From account number: "));
        Account to = bank.findAccount(readInt(in, "To account number: "));
        if (from.getAccountNo() == to.getAccountNo()) {
            throw new IllegalArgumentException("Source and destination accounts must be different.");
        }
        bank.transfer(from, to, readDouble(in, "Amount to transfer: "));
        System.out.println("Transfer successful.");
        System.out.println("Source balance: " + money(from.getBalance()));
        System.out.println("Destination balance: " + money(to.getBalance()));
    }

    private static void searchAccount(Bank bank, BufferedReader in) throws IOException {
        System.out.print("Enter account number or exact holder name: ");
        String query = in.readLine();
        if (query == null || query.trim().isEmpty()) {
            System.out.println("Search value cannot be empty.");
            return;
        }
        Account account;
        try {
            account = bank.findAccount(Integer.parseInt(query.trim()));
        } catch (NumberFormatException e) {
            account = bank.findAccount(query.trim());
        }
        System.out.println(account);
    }

    private static void save(Bank bank, FileStorage storage) throws IOException {
        storage.save(bank.getAccounts());
        System.out.println("Data saved to " + new java.io.File(DATA_FILE).getAbsolutePath());
    }

    private static int readInt(BufferedReader in, String prompt) throws IOException {
        System.out.print(prompt);
        return Integer.parseInt(requireLine(in).trim());
    }

    private static double readDouble(BufferedReader in, String prompt) throws IOException {
        System.out.print(prompt);
        double value = Double.parseDouble(requireLine(in).trim());
        if (!Double.isFinite(value)) throw new IllegalArgumentException("Enter a finite amount.");
        return value;
    }

    private static String requireLine(BufferedReader in) throws IOException {
        String value = in.readLine();
        if (value == null) throw new IOException("Input ended unexpectedly.");
        return value;
    }

    private static String money(double amount) {
        return String.format("%.2f", amount);
    }
}
