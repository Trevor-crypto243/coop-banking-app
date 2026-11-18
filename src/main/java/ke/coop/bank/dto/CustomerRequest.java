package ke.coop.bank.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CustomerRequest(

        @NotBlank(message = "first name is required")
        String firstName,

        @NotBlank(message = "last name is required")
        String lastName,

        @NotBlank(message = "national ID is required")
        @Pattern(regexp = "\\d{7,8}", message = "national ID should be 7 or 8 digits")
        String nationalId,

        @NotBlank(message = "email is required")
        @Email(message = "email is not valid")
        String email,

        @Pattern(regexp = "^(\\+254|0)[17]\\d{8}$", message = "use a valid Kenyan phone number e.g 0712345678")
        String phoneNumber,

        @NotNull(message = "opening balance is required")
        @DecimalMin(value = "0.00", message = "opening balance cannot be negative")
        @Digits(integer = 13, fraction = 2, message = "amount can only have 2 decimal places")
        BigDecimal openingBalance
) {
}
