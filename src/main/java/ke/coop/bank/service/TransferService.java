package ke.coop.bank.service;

import ke.coop.bank.dto.TransferRequest;
import ke.coop.bank.dto.TransferResponse;
import ke.coop.bank.exception.AccountNotFoundException;
import ke.coop.bank.exception.BadRequestException;
import ke.coop.bank.model.Account;
import ke.coop.bank.model.Transaction;
import ke.coop.bank.repository.AccountRepository;
import ke.coop.bank.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferService.class);

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransferService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    /*
     * Debit, credit and the transaction record all happen in one db transaction,
     * so if anything fails half way nothing is saved.
     */
    @Transactional
    public TransferResponse transfer(TransferRequest request) {
        String from = request.fromAccount().trim();
        String to = request.toAccount().trim();
        log.info("Transfer request: {} -> {}, amount {}", from, to, request.amount());

        if (from.equals(to)) {
            throw new BadRequestException("Source and destination account cannot be the same");
        }

        // always lock the smaller account number first, otherwise A->B and B->A
        // running at the same time can deadlock each other
        Account first = lock(from.compareTo(to) < 0 ? from : to);
        Account second = lock(from.compareTo(to) < 0 ? to : from);
        Account source = first.getAccountNumber().equals(from) ? first : second;
        Account destination = source == first ? second : first;

        source.debit(request.amount());
        destination.credit(request.amount());

        String narration = request.narration() == null || request.narration().isBlank()
                ? "Funds transfer to " + destination.getCustomer().getFullName()
                : request.narration();

        Transaction txn = transactionRepository.save(
                new Transaction(newReference(), from, to, request.amount(), narration));

        log.info("Transfer {} successful. {} balance: {}, {} balance: {}", txn.getReference(),
                from, source.getBalance(), to, destination.getBalance());

        return new TransferResponse(txn.getReference(), from, to, txn.getAmount(),
                source.getBalance(), destination.getBalance(), txn.getCreatedAt());
    }

    private Account lock(String accountNumber) {
        return accountRepository.findForUpdate(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    // e.g FT2611181430A1B2C3
    private String newReference() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmm"));
        return "FT" + time + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
}
