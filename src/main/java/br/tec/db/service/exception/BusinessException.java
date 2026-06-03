package br.tec.db.service.exception;

/**
 * Violação de regra de negócio (ex.: transferência para a mesma conta, saldo insuficiente).
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
