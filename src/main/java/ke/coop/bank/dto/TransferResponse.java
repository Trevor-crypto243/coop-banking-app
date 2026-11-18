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
public class TransferResponse {
    private String reference;
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private BigDecimal fromAccountBalance;
    private BigDecimal toAccountBalance;
    private LocalDateTime transactionDate;
}
