package com.bescobar.notes.shared.exception;

import org.springframework.http.HttpStatus;

@HttpStatusMapping(HttpStatus.BAD_REQUEST)
public abstract class InvalidDataException extends IllegalArgumentException {

    protected InvalidDataException() {
        super();
    }

    protected InvalidDataException(String message) {
        super(message);
    }

    protected InvalidDataException(String entityType, String field, String reason) {
        super(String.format("Invalid %s %s: %s", entityType, field, reason));
    }

    protected InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }
}