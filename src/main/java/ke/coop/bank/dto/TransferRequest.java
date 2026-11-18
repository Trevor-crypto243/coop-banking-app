package ke.coop.bank.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TransferRequest(

        @NotBlank(message = "source account is required")
        String fromAccount,

        @NotBlank(message = "destination account is required")
        String toAccount,

        @NotNull(message = "amount is required")
        @DecimalMin(value = "1.00", message = "minimum transfer amount is KES 1.00")
        @Digits(integer = 13, fraction = 2, message = "amount can only have 2 decimal places")
        BigDecimal amount,

        @Size(max = 100, message = "narration is too long (max 100 chars)")
        String narration
) {
}
