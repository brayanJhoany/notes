package com.bescobar.notes.user.domain.exception;

import com.bescobar.notes.shared.exception.EntityAlreadyExistsException;

public class UserAlreadyExistsException extends EntityAlreadyExistsException {

    public UserAlreadyExistsException(String field, Object value) {
        super("User", field, value);
    }

    public UserAlreadyExistsException(String email) {
        super(String.format("User with email %s already exists", email));
    }

}
