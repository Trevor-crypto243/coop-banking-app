package ke.coop.bank.dto;

import java.time.LocalDateTime;

/**
 * Every endpoint replies with this same envelope so clients only parse one shape.
 */
public record ApiResponse<T>(String status, String message, T data, LocalDateTime timestamp) {

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("SUCCESS", message, data, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> failed(String message, T details) {
        return new ApiResponse<>("FAILED", message, details, LocalDateTime.now());
    }
}
