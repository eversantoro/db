package br.tec.db.service;

import br.tec.db.api.dto.TransferRequest;
import br.tec.db.api.dto.TransferResponse;
import br.tec.db.domain.model.Account;
import br.tec.db.domain.repository.AccountRepository;
import br.tec.db.domain.repository.FinancialTransactionRepository;
import br.tec.db.notification.TransferCompletedEvent;
import br.tec.db.service.exception.BusinessException;
import br.tec.db.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários do {@link TransferService}.
 */
@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private FinancialTransactionRepository transactionRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private TransferService transferService;

    private Account from;
    private Account to;

    @BeforeEach
    void setUp() {
        from = new Account("conta-a", "Origem", new BigDecimal("1000.00"));
        to = new Account("conta-b", "Destino", new BigDecimal("200.00"));
    }

    @Test
    @DisplayName("Deve transferir entre contas e publicar evento")
    void shouldTransferSuccessfully() {
        when(accountRepository.findByIdForUpdate("conta-a")).thenReturn(Optional.of(from));
        when(accountRepository.findByIdForUpdate("conta-b")).thenReturn(Optional.of(to));
        when(accountRepository.save(any(Account.class))).thenAnswer(inv -> inv.getArgument(0));

        TransferRequest request = new TransferRequest("conta-a", "conta-b", new BigDecimal("150.00"));
        TransferResponse response = transferService.transfer(request);

        assertNotNull(response.transferId());
        assertEquals(new BigDecimal("850.00"), from.getBalance());
        assertEquals(new BigDecimal("350.00"), to.getBalance());

        ArgumentCaptor<TransferCompletedEvent> eventCaptor = ArgumentCaptor.forClass(TransferCompletedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(new BigDecimal("150.00"), eventCaptor.getValue().amount());
    }

    @Test
    @DisplayName("Deve rejeitar transferência para a mesma conta")
    void shouldRejectSameAccount() {
        TransferRequest request = new TransferRequest("conta-a", "conta-a", new BigDecimal("10.00"));
        assertThrows(BusinessException.class, () -> transferService.transfer(request));
    }

    @Test
    @DisplayName("Deve rejeitar quando conta não existe")
    void shouldRejectWhenAccountNotFound() {
        when(accountRepository.findByIdForUpdate(eq("conta-a"))).thenReturn(Optional.of(from));
        when(accountRepository.findByIdForUpdate(eq("inexistente"))).thenReturn(Optional.empty());

        TransferRequest request = new TransferRequest("conta-a", "inexistente", new BigDecimal("10.00"));
        assertThrows(ResourceNotFoundException.class, () -> transferService.transfer(request));
    }

    @Test
    @DisplayName("Deve rejeitar quando saldo insuficiente")
    void shouldRejectInsufficientBalance() {
        when(accountRepository.findByIdForUpdate("conta-a")).thenReturn(Optional.of(from));
        when(accountRepository.findByIdForUpdate("conta-b")).thenReturn(Optional.of(to));

        TransferRequest request = new TransferRequest("conta-a", "conta-b", new BigDecimal("5000.00"));
        assertThrows(IllegalStateException.class, () -> transferService.transfer(request));
    }
}
