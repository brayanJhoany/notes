package com.bescobar.notes.user.domain.exception;

import com.bescobar.notes.shared.exception.DomainException;
import com.bescobar.notes.shared.exception.HttpStatusMapping;
import org.springframework.http.HttpStatus;

@HttpStatusMapping(HttpStatus.UNAUTHORIZED)
public class UserAuthenticationException extends DomainException {

    public UserAuthenticationException(String message) {
        super(message);
    }
}
