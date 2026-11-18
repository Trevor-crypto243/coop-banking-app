package ke.coop.bank.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(String accountNumber, BigDecimal balance, BigDecimal amount) {
        super(String.format("Insufficient funds in account %s. Available: %,.2f, requested: %,.2f",
                accountNumber, balance, amount));
    }
}
