package com.bescobar.notes.noteshare.domain.exception;

import com.bescobar.notes.shared.exception.BusinessRuleViolationException;

public class NoteShareBusinessRuleException extends BusinessRuleViolationException {

    public NoteShareBusinessRuleException(String message) {
        super(message);
    }

    public NoteShareBusinessRuleException(String entityType, String rule) {
        super(entityType, rule);
    }
}
