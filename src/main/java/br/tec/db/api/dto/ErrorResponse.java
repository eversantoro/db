package br.tec.db.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Resposta padronizada de erro da API.
 */
@Schema(description = "Detalhes do erro")
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
