package com.bescobar.notes.user.application.port.in;

import com.bescobar.notes.user.application.dto.AuthResponseDto;

public interface  RefreshTokenInputPort {
    AuthResponseDto refreshToken(String refreshToken);
}
