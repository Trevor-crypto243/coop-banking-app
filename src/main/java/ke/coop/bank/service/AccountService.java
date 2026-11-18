package ke.coop.bank.service;

import ke.coop.bank.dto.BalanceResponse;
import ke.coop.bank.exception.AccountNotFoundException;
import ke.coop.bank.exception.BadRequestException;
import ke.coop.bank.model.Account;
import ke.coop.bank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public BalanceResponse getBalance(String accountNumber) {
        log.info("Balance enquiry for account {}", accountNumber);

        if (accountNumber == null || !accountNumber.matches("\\d{13}")) {
            throw new BadRequestException("Account number should be 13 digits");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account " + accountNumber + " does not exist"));

        DecimalFormat df = new DecimalFormat("#,##0.00");
        String formattedBalance = account.getCurrency() + " " + df.format(account.getBalance());

        return BalanceResponse.builder()
                .accountNumber(account.getAccountNumber())
                .accountName(account.getCustomer().getFullName())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .formattedBalance(formattedBalance)
                .asAt(LocalDateTime.now().withNano(0))
                .build();
    }
}
