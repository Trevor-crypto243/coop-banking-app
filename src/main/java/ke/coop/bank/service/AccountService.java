package ke.coop.bank.service;

import ke.coop.bank.dto.BalanceResponse;
import ke.coop.bank.exception.AccountNotFoundException;
import ke.coop.bank.exception.BadRequestException;
import ke.coop.bank.model.Account;
import ke.coop.bank.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public BalanceResponse getBalance(String accountNumber) {
        if (!accountNumber.matches("\\d{13}")) {
            throw new BadRequestException("Account number should be 13 digits");
        }
        log.info("Balance enquiry for account {}", accountNumber);

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        String formatted = String.format("%s %,.2f", account.getCurrency(), account.getBalance());

        return new BalanceResponse(account.getAccountNumber(), account.getCustomer().getFullName(),
                account.getBalance(), account.getCurrency(), formatted, LocalDateTime.now());
    }
}
