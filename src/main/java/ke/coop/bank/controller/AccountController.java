package ke.coop.bank.controller;

import ke.coop.bank.dto.ApiResponse;
import ke.coop.bank.dto.BalanceResponse;
import ke.coop.bank.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    //Accounts controller
    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<ApiResponse<BalanceResponse>> getBalance(@PathVariable String accountNumber) {
        log.info("GET /api/v1/accounts/{}/balance", accountNumber);
        BalanceResponse balance = accountService.getBalance(accountNumber);
        String message = "Dear " + balance.getAccountName() + ", your account balance is " + balance.getFormattedBalance();
        return ResponseEntity.ok(new ApiResponse<>("SUCCESS", message, balance));
    }
}
