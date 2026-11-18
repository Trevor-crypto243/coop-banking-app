package ke.coop.bank.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerRequest {

    @NotBlank(message = "first name is required")
    private String firstName;

    @NotBlank(message = "last name is required")
    private String lastName;

    @NotBlank(message = "national ID is required")
    @Pattern(regexp = "\\d{7,8}", message = "national ID should be 7 or 8 digits")
    private String nationalId;

    @NotBlank(message = "email is required")
    @Email(message = "email is not valid")
    private String email;

    //kenyan numbers only e.g 0712345678 or +254712345678
    @Pattern(regexp = "^(\\+254|0)[17]\\d{8}$", message = "phone number is not valid")
    private String phoneNumber;

    @NotNull(message = "opening balance is required")
    @DecimalMin(value = "0.0", message = "opening balance cannot be negative")
    private BigDecimal openingBalance;
}
