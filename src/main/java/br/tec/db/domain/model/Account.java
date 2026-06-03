package br.tec.db.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * Representa uma conta bancária de cliente com saldo e controle de concorrência otimista.
 */
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    private Long version;

    protected Account() {
    }

    public Account(String id, String name, BigDecimal balance) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.balance = Objects.requireNonNull(balance, "balance");
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Long getVersion() {
        return version;
    }

    /**
     * Credita valor na conta (entrada de fundos).
     */
    public void credit(BigDecimal amount) {
        validatePositive(amount);
        this.balance = this.balance.add(amount);
    }

    /**
     * Debita valor da conta (saída de fundos).
     *
     * @throws IllegalStateException se saldo insuficiente
     */
    public void debit(BigDecimal amount) {
        validatePositive(amount);
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Saldo insuficiente na conta " + id);
        }
        this.balance = this.balance.subtract(amount);
    }

    private static void validatePositive(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Valor da operação deve ser positivo");
        }
    }
}
