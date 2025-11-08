package com.bescobar.notes.shared.exception;

import org.springframework.http.HttpStatus;

@HttpStatusMapping(HttpStatus.NOT_FOUND)
public abstract class EntityNotFoundException extends DomainException {

    protected EntityNotFoundException() {
        super();
    }

    protected EntityNotFoundException(String message) {
        super(message);
    }

    protected EntityNotFoundException(String entityType, Object id) {
        super(String.format("%s not found with id: %s", entityType, id));
    }

    protected EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}