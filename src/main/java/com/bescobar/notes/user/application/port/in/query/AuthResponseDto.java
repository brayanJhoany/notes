package com.bescobar.notes.user.application.port.in.query;

import com.bescobar.notes.user.domain.model.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthResponseDto {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private String refreshToken;
    private User user;
}