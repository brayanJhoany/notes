package com.bescobar.notes.user.application.port.in;

import com.bescobar.notes.user.application.dto.AuthResponseDto;

public interface  RefreshTokenUseCase {
    AuthResponseDto refreshToken(String refreshToken);
}
