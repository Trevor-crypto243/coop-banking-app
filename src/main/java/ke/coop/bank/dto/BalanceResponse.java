package ke.coop.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceResponse {
    private String accountNumber;
    private String accountName;
    private BigDecimal balance;
    private String currency;
    private String formattedBalance;
    private LocalDateTime asAt;
}
