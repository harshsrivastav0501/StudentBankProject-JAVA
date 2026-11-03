package bank.model;

import bank.exception.InsufficientFundsException;
import java.io.Serializable;

/** Abstract base class: shared state (protected), constructors, overloaded methods, abstract methods. */
public abstract class Account implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String BANK_NAME = "Student Bank";   // static final constant
    private static int nextAccountNo = 1001;                  // static member shared by all accounts

    private final int accountNo;        // private + final
    private final String holderName;
    protected double balance;           // protected: visible to subclasses

    public Account(String holderName) {
        this(holderName, 0.0);          // constructor chaining with this(...)
    }

    public Account(String holderName, double initialDeposit) {
        if (holderName == null || holderName.trim().isEmpty()) {
            throw new IllegalArgumentException("Account holder name cannot be empty");
        }
        if (!Double.isFinite(initialDeposit) || initialDeposit < 0) {
            throw new IllegalArgumentException("Initial deposit must be a finite, non-negative amount");
        }
        this.accountNo = nextNumber();
        this.holderName = holderName.trim();
        this.balance = initialDeposit;
    }

    private static synchronized int nextNumber() {
        return nextAccountNo++;
    }

    /** Used after loading from disk so new accounts never reuse a saved number. */
    public static synchronized void reserveNumbersUpTo(int number) {
        if (nextAccountNo <= number) {
            nextAccountNo = number + 1;
        }
    }

    public static String getBankName() {
        return BANK_NAME;
    }

    // Method overloading
    public synchronized void deposit(double amount) {
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Deposit must be positive");
        }
        balance += amount;
    }

    public void deposit(double amount, String note) {
        deposit(amount);
        System.out.println("Deposit note: " + note);
    }

    public synchronized void withdraw(double amount) throws InsufficientFundsException {
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Withdrawal must be positive");
        }
        double available = balance + overdraftLimit();
        if (amount > available) {
            throw new InsufficientFundsException(
                    "Insufficient funds in account " + accountNo, amount - available);
        }
        balance -= amount;
    }

    public synchronized double getBalance() {
        return balance;
    }

    public int getAccountNo() {
        return accountNo;
    }

    public String getHolderName() {
        return holderName;
    }

    // Abstract methods: each subclass must provide its own version
    public abstract String accountType();
    protected abstract double overdraftLimit();

    @Override
    public String toString() {
        double rounded = Math.round(getBalance() * 100) / 100.0;
        return accountType() + " | No: " + accountNo + " | " + holderName + " | Balance: " + rounded;
    }
}
