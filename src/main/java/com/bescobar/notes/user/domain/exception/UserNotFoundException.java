package com.bescobar.notes.user.domain.exception;

import com.bescobar.notes.shared.exception.EntityNotFoundException;

public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException(Long id) {
        super("User", id);
    }

    public UserNotFoundException(String email) {
        super(String.format("User not found with email: %s", email));
    }

}
