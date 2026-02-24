package com.bescobar.notes.user.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
