package br.tec.db.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Payload para cadastro de conta (ID, nome e saldo inicial).
 */
@Schema(description = "Dados para criação de conta bancária")
public record AccountRequest(
        @Schema(description = "Identificador único da conta", example = "conta-001")
        @NotBlank @Size(max = 36) String id,

        @Schema(description = "Nome do titular", example = "Maria Silva")
        @NotBlank @Size(max = 200) String name,

        @Schema(description = "Saldo inicial", example = "1000.00")
        @NotNull @DecimalMin(value = "0.00", inclusive = true) BigDecimal initialBalance
) {
}
