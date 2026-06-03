package br.tec.db.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Payload para transferência entre duas contas.
 */
@Schema(description = "Solicitação de transferência de fundos")
public record TransferRequest(
        @Schema(description = "ID da conta de origem", example = "conta-001")
        @NotBlank String fromAccountId,

        @Schema(description = "ID da conta de destino", example = "conta-002")
        @NotBlank String toAccountId,

        @Schema(description = "Valor a transferir", example = "150.50")
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount
) {
}
