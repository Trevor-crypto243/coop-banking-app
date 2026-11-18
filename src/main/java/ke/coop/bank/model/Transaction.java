package ke.coop.bank.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String reference;

    private String sourceAccount;
    private String destinationAccount;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

    private String narration;

    private LocalDateTime createdAt = LocalDateTime.now().withNano(0);

    public Transaction() {
    }

    public Transaction(String reference, String sourceAccount, String destinationAccount,
                       BigDecimal amount, String narration) {
        this.reference = reference;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
        this.narration = narration;
    }

    public Long getId() { return id; }
    public String getReference() { return reference; }
    public String getSourceAccount() { return sourceAccount; }
    public String getDestinationAccount() { return destinationAccount; }
    public BigDecimal getAmount() { return amount; }
    public String getNarration() { return narration; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
