package br.tec.db.api.dto;

import br.tec.db.domain.model.Account;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Representação de conta retornada pela API.
 */
@Schema(description = "Conta bancária")
public record AccountResponse(
        String id,
        String name,
        BigDecimal balance,
        Instant createdAt
) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getName(),
                account.getBalance(),
                account.getCreatedAt());
    }
}
