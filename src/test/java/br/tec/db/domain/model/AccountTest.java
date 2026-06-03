package br.tec.db.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testes unitários da entidade {@link Account}.
 */
class AccountTest {

    @Test
    @DisplayName("Deve creditar valor ao saldo")
    void shouldCredit() {
        Account account = new Account("1", "Teste", new BigDecimal("100.00"));
        account.credit(new BigDecimal("50.00"));
        assertEquals(new BigDecimal("150.00"), account.getBalance());
    }

    @Test
    @DisplayName("Deve debitar quando há saldo suficiente")
    void shouldDebit() {
        Account account = new Account("1", "Teste", new BigDecimal("100.00"));
        account.debit(new BigDecimal("40.00"));
        assertEquals(new BigDecimal("60.00"), account.getBalance());
    }

    @Test
    @DisplayName("Deve rejeitar débito com saldo insuficiente")
    void shouldRejectDebitWhenInsufficientBalance() {
        Account account = new Account("1", "Teste", new BigDecimal("10.00"));
        assertThrows(IllegalStateException.class, () -> account.debit(new BigDecimal("20.00")));
    }

    @Test
    @DisplayName("Deve rejeitar valor zero ou negativo")
    void shouldRejectNonPositiveAmount() {
        Account account = new Account("1", "Teste", new BigDecimal("10.00"));
        assertThrows(IllegalArgumentException.class, () -> account.debit(BigDecimal.ZERO));
    }
}
