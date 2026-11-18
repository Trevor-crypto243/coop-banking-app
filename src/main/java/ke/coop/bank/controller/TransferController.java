package ke.coop.bank.controller;

import jakarta.validation.Valid;
import ke.coop.bank.dto.ApiResponse;
import ke.coop.bank.dto.TransferRequest;
import ke.coop.bank.dto.TransferResponse;
import ke.coop.bank.service.TransferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    //Transfer controller
    @PostMapping
    public ResponseEntity<ApiResponse<TransferResponse>> transfer(@Valid @RequestBody TransferRequest request) {
        log.info("POST /api/v1/transfers");
        TransferResponse result = transferService.transfer(request);
        String message = String.format("KES %,.2f sent to %s. Ref: %s",result.getAmount(), result.getToAccount(), result.getReference());
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", message, result));
    }
}
