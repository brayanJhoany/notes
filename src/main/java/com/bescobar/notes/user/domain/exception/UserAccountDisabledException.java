package com.bescobar.notes.user.domain.exception;

import org.springframework.http.HttpStatus;

import com.bescobar.notes.shared.exception.DomainException;
import com.bescobar.notes.shared.exception.HttpStatusMapping;

@HttpStatusMapping(HttpStatus.FORBIDDEN)
public class UserAccountDisabledException extends DomainException {

    public UserAccountDisabledException(String email) {
        super(String.format("The user account with email %s is disabled", email));
    }
}
