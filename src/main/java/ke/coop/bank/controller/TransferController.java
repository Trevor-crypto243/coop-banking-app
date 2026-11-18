package ke.coop.bank.controller;

import jakarta.validation.Valid;
import ke.coop.bank.dto.ApiResponse;
import ke.coop.bank.dto.TransferRequest;
import ke.coop.bank.dto.TransferResponse;
import ke.coop.bank.service.TransferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    //Transfer controller
    @PostMapping
    public ResponseEntity<ApiResponse<TransferResponse>> transfer(@Valid @RequestBody TransferRequest request) {
        TransferResponse result = transferService.transfer(request);
        String message = String.format("KES %,.2f sent to %s. Ref: %s",result.amount(), result.toAccount(), result.reference());
        return ResponseEntity.ok(ApiResponse.success(message, result));
    }
}
