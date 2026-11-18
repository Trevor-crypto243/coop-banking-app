package ke.coop.bank.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String reference;

    private String sourceAccount;
    private String destinationAccount;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

    private String narration;
    private String status;
    private LocalDateTime createdAt;
}
