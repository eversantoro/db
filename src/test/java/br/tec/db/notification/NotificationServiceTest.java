package br.tec.db.notification;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Testes unitários do {@link NotificationService}.
 */
class NotificationServiceTest {

    @Test
    @DisplayName("Deve processar evento de transferência sem erro")
    void shouldHandleTransferEvent() {
        NotificationService service = new NotificationService();
        TransferCompletedEvent event = new TransferCompletedEvent(
                UUID.randomUUID(),
                "conta-001",
                "Ana",
                "conta-002",
                "Bruno",
                new BigDecimal("100.00"));

        assertDoesNotThrow(() -> service.onTransferCompleted(event));
    }
}
