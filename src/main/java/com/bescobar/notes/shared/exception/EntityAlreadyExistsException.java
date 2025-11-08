package com.bescobar.notes.shared.exception;

import org.springframework.http.HttpStatus;

@HttpStatusMapping(HttpStatus.CONFLICT)
public abstract class EntityAlreadyExistsException extends DomainException {

    protected EntityAlreadyExistsException() {
        super();
    }

    protected EntityAlreadyExistsException(String message) {
        super(message);
    }

    protected EntityAlreadyExistsException(String entityType, String field, Object value) {
        super(String.format("%s with %s '%s' already exists", entityType, field, value));
    }

    protected EntityAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}