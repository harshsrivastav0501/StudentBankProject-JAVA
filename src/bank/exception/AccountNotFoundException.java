package bank.exception;

/** Unchecked exception: a programming/lookup error that need not be declared. */
public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
