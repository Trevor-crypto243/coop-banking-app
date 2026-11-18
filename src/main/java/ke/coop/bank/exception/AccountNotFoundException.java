package ke.coop.bank.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String accountNumber) {
        super("Account " + accountNumber + " does not exist");
    }
}
