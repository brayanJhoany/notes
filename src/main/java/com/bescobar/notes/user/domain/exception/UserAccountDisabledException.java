package com.bescobar.notes.user.domain.exception;

import com.bescobar.notes.shared.exception.DomainException;

public class UserAccountDisabledException extends DomainException {

    public UserAccountDisabledException(String email) {
        super(String.format("The user account with email %s is disabled", email));
    }
}
