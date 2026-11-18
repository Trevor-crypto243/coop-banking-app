package ke.coop.bank.dto;

import ke.coop.bank.model.Account;
import ke.coop.bank.model.Customer;

import java.math.BigDecimal;

public record CustomerResponse(Long customerId, String fullName, String email,String accountNumber, BigDecimal balance, String currency) {

    public static CustomerResponse from(Customer customer, Account account) {
        return new CustomerResponse(customer.getId(), customer.getFullName(), customer.getEmail(), account.getAccountNumber(), account.getBalance(), account.getCurrency());
    }
}
