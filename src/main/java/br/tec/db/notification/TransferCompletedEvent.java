package br.tec.db.notification;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Evento publicado após conclusão bem-sucedida de uma transferência.
 */
public record TransferCompletedEvent(
        UUID transferId,
        String fromAccountId,
        String fromAccountHolderName,
        String toAccountId,
        String toAccountHolderName,
        BigDecimal amount
) {
}
