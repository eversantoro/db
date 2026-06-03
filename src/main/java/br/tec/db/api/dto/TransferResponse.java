package br.tec.db.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Resultado de transferência concluída com sucesso.
 */
@Schema(description = "Transferência realizada")
public record TransferResponse(
        UUID transferId,
        String fromAccountId,
        String toAccountId,
        BigDecimal amount,
        Instant completedAt
) {
}
