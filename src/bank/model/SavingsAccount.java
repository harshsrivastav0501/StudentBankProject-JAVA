package bank.model;

/** Inheritance: SavingsAccount is-a Account. No overdraft. */
public class SavingsAccount extends Account {
    private static final long serialVersionUID = 6514758780446661323L;   // keeps old bank-data.ser readable

    public SavingsAccount(String holderName, double initialDeposit) {
        super(holderName, initialDeposit);   // calls the superclass constructor
    }

    @Override
    public String accountType() {
        return "Savings";
    }

    @Override
    protected double overdraftLimit() {
        return 0;
    }
}
