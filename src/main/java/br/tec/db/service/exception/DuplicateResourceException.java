package br.tec.db.service.exception;

/**
 * Tentativa de criar recurso que já existe (ex.: conta com ID duplicado).
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
