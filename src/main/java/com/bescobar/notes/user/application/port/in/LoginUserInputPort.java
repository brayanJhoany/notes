package com.bescobar.notes.user.application.port.in;

import com.bescobar.notes.user.application.dto.AuthResponseDto;
import com.bescobar.notes.user.application.dto.LoginCommand;

public interface LoginUserInputPort {
    AuthResponseDto login(LoginCommand loginCommand);
}
