package bank.service;

import bank.exception.AccountNotFoundException;
import bank.exception.InsufficientFundsException;
import bank.model.Account;
import java.util.ArrayList;
import java.util.Iterator;

/** Holds accounts. Uses collections, iterators, recursion, varargs and synchronized methods. */
public class Bank {
    public static final String CURRENCY = "INR";

    private final ArrayList accounts = new ArrayList();   // raw ArrayList (generics are not in the syllabus)

    public synchronized void addAccount(Account account) {
        accounts.add(account);
    }

    // Overloaded finders; each returns an object reference
    public synchronized Account findAccount(int accountNo) {
        for (int i = 0; i < accounts.size(); i++) {
            Account a = (Account) accounts.get(i);
            if (a.getAccountNo() == accountNo) {
                return a;
            }
        }
        throw new AccountNotFoundException("No account with number " + accountNo);
    }

    public synchronized Account findAccount(String holderName) {
        Iterator it = accounts.iterator();
        while (it.hasNext()) {
            Account a = (Account) it.next();
            if (a.getHolderName().equalsIgnoreCase(holderName)) {   // String comparison
                return a;
            }
        }
        throw new AccountNotFoundException("No account for holder " + holderName);
    }

    // Objects passed as parameters
    public synchronized void transfer(Account from, Account to, double amount)
            throws InsufficientFundsException {
        from.withdraw(amount);
        to.deposit(amount);
    }

    public synchronized double totalDeposits() {
        return sumFrom(0);
    }

    // Recursion
    private double sumFrom(int index) {
        if (index >= accounts.size()) {
            return 0;
        }
        Account a = (Account) accounts.get(index);
        return a.getBalance() + sumFrom(index + 1);
    }

    // Variable-length arguments
    public static double total(double... amounts) {
        double sum = 0;
        for (double x : amounts) {
            sum += x;
        }
        return sum;
    }

    // Iterator + StringBuffer
    public synchronized String statement() {
        StringBuffer sb = new StringBuffer();
        sb.append("Currency: ").append(CURRENCY).append("\n");
        Iterator it = accounts.iterator();
        while (it.hasNext()) {
            sb.append(it.next()).append("\n");
        }
        return sb.toString();
    }

    public synchronized int size() {
        return accounts.size();
    }

    public synchronized ArrayList getAccounts() {
        return accounts;
    }

    public synchronized void loadAccounts(ArrayList saved) {
        accounts.clear();
        for (int i = 0; i < saved.size(); i++) {
            Account a = (Account) saved.get(i);
            Account.reserveNumbersUpTo(a.getAccountNo());
            accounts.add(a);
        }
    }
}
