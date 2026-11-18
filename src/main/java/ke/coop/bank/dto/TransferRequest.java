package ke.coop.bank.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {

    @NotBlank(message = "source account is required")
    private String fromAccount;

    @NotBlank(message = "destination account is required")
    private String toAccount;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "1.0", message = "minimum transfer amount is KES 1")
    private BigDecimal amount;

    private String narration;
}
