package com.bescobar.notes.shared.exception;

import org.springframework.http.HttpStatus;

@HttpStatusMapping(HttpStatus.INTERNAL_SERVER_ERROR)
public abstract class DomainException extends RuntimeException {

    protected DomainException() {
        super();
    }

    protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }

    protected DomainException(Throwable cause) {
        super(cause);
    }
}