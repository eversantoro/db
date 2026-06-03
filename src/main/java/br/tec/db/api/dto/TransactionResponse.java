package br.tec.db.api.dto;

import br.tec.db.domain.model.FinancialTransaction;
import br.tec.db.domain.model.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Movimentação financeira no extrato da conta.
 */
@Schema(description = "Movimentação financeira")
public record TransactionResponse(
        UUID id,
        String accountId,
        TransactionType type,
        BigDecimal amount,
        BigDecimal balanceAfter,
        String description,
        UUID transferId,
        Instant occurredAt
) {
    public static TransactionResponse from(FinancialTransaction tx) {
        return new TransactionResponse(
                tx.getId(),
                tx.getAccountId(),
                tx.getType(),
                tx.getAmount(),
                tx.getBalanceAfter(),
                tx.getDescription(),
                tx.getTransferId(),
                tx.getOccurredAt());
    }
}
