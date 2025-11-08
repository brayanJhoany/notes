package com.bescobar.notes.shared.exception;

import org.springframework.http.HttpStatus;

@HttpStatusMapping(HttpStatus.UNPROCESSABLE_ENTITY)
public abstract class BusinessRuleViolationException extends DomainException {

    protected BusinessRuleViolationException() {
        super();
    }

    protected BusinessRuleViolationException(String message) {
        super(message);
    }

    protected BusinessRuleViolationException(String entityType, String rule) {
        super(String.format("%s violates business rule: %s", entityType, rule));
    }

    protected BusinessRuleViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}