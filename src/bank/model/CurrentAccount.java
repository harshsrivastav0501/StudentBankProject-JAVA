package bank.model;

/** Inheritance with method overriding and super.toString(). Allows overdraft. */
public class CurrentAccount extends Account {
    private static final long serialVersionUID = -578478995396803732L;
    private static final double OVERDRAFT_LIMIT = 5000.0;

    public CurrentAccount(String holderName) {
        super(holderName);
    }

    public CurrentAccount(String holderName, double initialDeposit) {
        super(holderName, initialDeposit);
    }

    @Override
    public String accountType() {
        return "Current";
    }

    @Override
    protected double overdraftLimit() {
        return OVERDRAFT_LIMIT;
    }

    @Override
    public String toString() {
        return super.toString() + " | Overdraft: " + OVERDRAFT_LIMIT;
    }
}
