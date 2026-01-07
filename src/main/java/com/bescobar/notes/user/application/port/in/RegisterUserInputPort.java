package com.bescobar.notes.user.application.port.in;

import com.bescobar.notes.user.application.dto.AuthResponseDto;
import com.bescobar.notes.user.domain.model.User;

public interface RegisterUserInputPort {
    AuthResponseDto registerUser(User user);
}
