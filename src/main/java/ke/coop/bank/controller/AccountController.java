package ke.coop.bank.controller;

import ke.coop.bank.dto.ApiResponse;
import ke.coop.bank.dto.BalanceResponse;
import ke.coop.bank.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<ApiResponse<BalanceResponse>> getBalance(@PathVariable String accountNumber) {
        BalanceResponse balance = accountService.getBalance(accountNumber);
        String message = "Dear " + balance.accountName() + ", your account balance is " + balance.formattedBalance();
        return ResponseEntity.ok(ApiResponse.success(message, balance));
    }
}
