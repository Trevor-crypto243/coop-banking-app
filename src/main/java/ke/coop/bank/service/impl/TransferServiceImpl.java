package ke.coop.bank.service.impl;

import ke.coop.bank.dto.TransferRequest;
import ke.coop.bank.dto.TransferResponse;
import ke.coop.bank.exception.AccountNotFoundException;
import ke.coop.bank.exception.BadRequestException;
import ke.coop.bank.exception.InsufficientFundsException;
import ke.coop.bank.model.Account;
import ke.coop.bank.model.Transaction;
import ke.coop.bank.service.TransferService;
import ke.coop.bank.repository.AccountRepository;
import ke.coop.bank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    //@Transactional so that if anything fails the debit and credit are both rolled back
    @Override
    @Transactional
    public TransferResponse transfer(TransferRequest request) {
        log.info("Transfer of {} from {} to {}", request.getAmount(), request.getFromAccount(), request.getToAccount());

        if (request.getFromAccount().equals(request.getToAccount())) {
            throw new BadRequestException("Source and destination account cannot be the same");
        }

        Account fromAccount = accountRepository.findByAccountNumber(request.getFromAccount())
                .orElseThrow(() -> new AccountNotFoundException("Account " + request.getFromAccount() + " does not exist"));
        Account toAccount = accountRepository.findByAccountNumber(request.getToAccount())
                .orElseThrow(() -> new AccountNotFoundException("Account " + request.getToAccount() + " does not exist"));

        BigDecimal amount = request.getAmount();

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds in account " + fromAccount.getAccountNumber() + ". Available balance is KES " + fromAccount.getBalance());
        }

        //debit sender, credit receiver
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        fromAccount.setUpdatedAt(LocalDateTime.now());
        toAccount.setBalance(toAccount.getBalance().add(amount));
        toAccount.setUpdatedAt(LocalDateTime.now());

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction transaction = new Transaction();
        transaction.setReference(generateReference());
        transaction.setSourceAccount(fromAccount.getAccountNumber());
        transaction.setDestinationAccount(toAccount.getAccountNumber());
        transaction.setAmount(amount);
        if (request.getNarration() != null && !request.getNarration().isBlank()) {
            transaction.setNarration(request.getNarration());
        } else {
            transaction.setNarration("Funds transfer to " + toAccount.getCustomer().getFullName());
        }
        transaction.setStatus("SUCCESS");
        transaction.setCreatedAt(LocalDateTime.now().withNano(0));
        transactionRepository.save(transaction);

        log.info("Transfer successful, ref {}. New balances: {} = {}, {} = {}", transaction.getReference(), fromAccount.getAccountNumber(), fromAccount.getBalance(), toAccount.getAccountNumber(), toAccount.getBalance());

        return TransferResponse.builder()
                .reference(transaction.getReference())
                .fromAccount(fromAccount.getAccountNumber())
                .toAccount(toAccount.getAccountNumber())
                .amount(amount)
                .fromAccountBalance(fromAccount.getBalance())
                .toAccountBalance(toAccount.getBalance())
                .transactionDate(transaction.getCreatedAt())
                .build();
    }

    private String generateReference() {
        return "FT" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }
}
