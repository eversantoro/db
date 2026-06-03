package br.tec.db.service.exception;

/**
 * Indica que um recurso solicitado não existe (ex.: conta inexistente).
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
