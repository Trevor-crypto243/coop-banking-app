package ke.coop.bank.service;

import ke.coop.bank.dto.CustomerRequest;
import ke.coop.bank.dto.CustomerResponse;
import ke.coop.bank.exception.BadRequestException;
import ke.coop.bank.model.Account;
import ke.coop.bank.model.Customer;
import ke.coop.bank.repository.AccountRepository;
import ke.coop.bank.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        log.info("Creating customer {} {}", request.getFirstName(), request.getLastName());

        if (customerRepository.existsByNationalId(request.getNationalId())) {
            throw new BadRequestException("A customer with this national ID already exists");
        }
        if (customerRepository.existsByEmail(request.getEmail().toLowerCase())) {
            throw new BadRequestException("A customer with this email already exists");
        }

        Customer customer = new Customer();
        customer.setFirstName(request.getFirstName().trim());
        customer.setLastName(request.getLastName().trim());
        customer.setNationalId(request.getNationalId());
        customer.setEmail(request.getEmail().toLowerCase());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setCreatedAt(LocalDateTime.now());
        customer = customerRepository.save(customer);

        //open an account for the customer with the opening balance
        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(request.getOpeningBalance());
        account.setCustomer(customer);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        account = accountRepository.save(account);

        log.info("Customer saved with id {} and account number {}", customer.getId(), account.getAccountNumber());

        return CustomerResponse.builder()
                .customerId(customer.getId())
                .fullName(customer.getFullName())
                .email(customer.getEmail())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .build();
    }

    //account numbers are 13 digits and start with 01
    private String generateAccountNumber() {
        Random random = new Random();
        String accountNumber = "01" + String.format("%011d", random.nextLong(100000000000L));
        while (accountRepository.existsByAccountNumber(accountNumber)) {
            accountNumber = "01" + String.format("%011d", random.nextLong(100000000000L));
        }
        return accountNumber;
    }
}
