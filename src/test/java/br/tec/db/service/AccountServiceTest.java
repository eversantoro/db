package br.tec.db.service;

import br.tec.db.api.dto.AccountRequest;
import br.tec.db.api.dto.AccountResponse;
import br.tec.db.domain.model.Account;
import br.tec.db.domain.repository.AccountRepository;
import br.tec.db.domain.repository.FinancialTransactionRepository;
import br.tec.db.service.exception.DuplicateResourceException;
import br.tec.db.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários do {@link AccountService}.
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private FinancialTransactionRepository transactionRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    @DisplayName("Deve criar conta com saldo inicial")
    void shouldCreateAccount() {
        AccountRequest request = new AccountRequest("nova", "Cliente", new BigDecimal("500.00"));
        when(accountRepository.existsById("nova")).thenReturn(false);
        when(accountRepository.save(org.mockito.ArgumentMatchers.any(Account.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        AccountResponse response = accountService.create(request);

        assertEquals("nova", response.id());
        assertEquals(new BigDecimal("500.00"), response.balance());
        verify(accountRepository).save(org.mockito.ArgumentMatchers.any(Account.class));
    }

    @Test
    @DisplayName("Deve rejeitar ID duplicado")
    void shouldRejectDuplicateId() {
        when(accountRepository.existsById("dup")).thenReturn(true);
        AccountRequest request = new AccountRequest("dup", "X", BigDecimal.TEN);
        assertThrows(DuplicateResourceException.class, () -> accountService.create(request));
    }

    @Test
    @DisplayName("Deve lançar exceção quando conta não encontrada")
    void shouldThrowWhenNotFound() {
        when(accountRepository.findById("x")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> accountService.findById("x"));
    }
}
