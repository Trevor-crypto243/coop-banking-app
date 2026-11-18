package ke.coop.bank.service;

import ke.coop.bank.dto.CustomerRequest;
import ke.coop.bank.dto.CustomerResponse;
import ke.coop.bank.exception.BadRequestException;
import ke.coop.bank.model.Account;
import ke.coop.bank.model.Customer;
import ke.coop.bank.repository.AccountRepository;
import ke.coop.bank.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    public CustomerService(CustomerRepository customerRepository, AccountRepository accountRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        log.info("Onboarding new customer, nationalId={}", mask(request.nationalId()));

        if (customerRepository.existsByNationalId(request.nationalId())) {
            throw new BadRequestException("A customer with this national ID already exists");
        }
        if (customerRepository.existsByEmail(request.email().toLowerCase())) {
            throw new BadRequestException("A customer with this email already exists");
        }

        Customer customer = new Customer(
                request.firstName().trim(),
                request.lastName().trim(),
                request.nationalId(),
                request.email().toLowerCase(),
                request.phoneNumber());

        Account account = new Account(generateAccountNumber(), request.openingBalance());
        customer.addAccount(account);
        customerRepository.save(customer);

        log.info("Customer {} created with account {}", customer.getId(), account.getAccountNumber());
        return CustomerResponse.from(customer, account);
    }

    // Co-op style 13 digit account number: 01 + 11 random digits
    private String generateAccountNumber() {
        String number;
        do {
            number = "01" + ThreadLocalRandom.current().nextLong(10_000_000_000L, 99_999_999_999L);
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }

    private String mask(String value) {
        return "****" + value.substring(value.length() - 3);
    }
}
