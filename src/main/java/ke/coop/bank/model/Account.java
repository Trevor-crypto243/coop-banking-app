package ke.coop.bank.model;

import jakarta.persistence.*;
import ke.coop.bank.exception.InsufficientFundsException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 13)
    private String accountNumber;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    private String currency = "KES";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private LocalDateTime updatedAt = LocalDateTime.now();

    public Account() {
    }

    public Account(String accountNumber, BigDecimal openingBalance) {
        this.accountNumber = accountNumber;
        this.balance = openingBalance;
    }

    public void debit(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException(accountNumber, balance, amount);
        }
        balance = balance.subtract(amount);
        updatedAt = LocalDateTime.now();
    }

    public void credit(BigDecimal amount) {
        balance = balance.add(amount);
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getAccountNumber() { return accountNumber; }
    public BigDecimal getBalance() { return balance; }
    public String getCurrency() { return currency; }
    public Customer getCustomer() { return customer; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
