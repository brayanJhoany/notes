package com.bescobar.notes.user.application.port.in;

import com.bescobar.notes.user.application.dto.AuthResponseDto;
import com.bescobar.notes.user.application.dto.LoginCommand;

public interface LoginUserUseCase {
    AuthResponseDto login(LoginCommand loginCommand);
}
