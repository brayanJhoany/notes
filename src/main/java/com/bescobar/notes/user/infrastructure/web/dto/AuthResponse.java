package com.bescobar.notes.user.infrastructure.web.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {

    private final String accessToken;
    private final String refreshToken;
    private final UserResponse user;

    public UserResponse getUser() {
        return UserResponse.copyOf(user);
    }

    public static class AuthResponseBuilder {
        public AuthResponseBuilder user(UserResponse user) {
            this.user = UserResponse.copyOf(user);
            return this;
        }
    }
}
