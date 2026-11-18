package ke.coop.bank.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String nationalId;

    @Column(unique = true, nullable = false)
    private String email;

    private String phoneNumber;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Account> accounts = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();

    public Customer() {
    }

    public Customer(String firstName, String lastName, String nationalId, String email, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.nationalId = nationalId;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void addAccount(Account account) {
        accounts.add(account);
        account.setCustomer(this);
    }

    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getNationalId() { return nationalId; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public List<Account> getAccounts() { return accounts; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
