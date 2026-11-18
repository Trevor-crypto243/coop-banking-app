package ke.coop.bank.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferResponse(String reference,
                               String fromAccount,
                               String toAccount,
                               BigDecimal amount,
                               BigDecimal fromAccountBalance,
                               BigDecimal toAccountBalance,
                               LocalDateTime transactionDate) {
}
