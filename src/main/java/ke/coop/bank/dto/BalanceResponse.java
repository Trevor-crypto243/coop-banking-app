package ke.coop.bank.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BalanceResponse(String accountNumber,
                              String accountName,
                              BigDecimal balance,
                              String currency,
                              String formattedBalance,
                              LocalDateTime asAt) {
}
