package com.bescobar.notes.user.application.port.in;

import com.bescobar.notes.user.application.port.in.query.AuthResponseDto;
import com.bescobar.notes.user.application.port.in.command.LoginCommand;

public interface LoginUserInputPort {
    AuthResponseDto login(LoginCommand loginCommand);
}
