package com.bescobar.notes.user.application.dto;

import com.bescobar.notes.user.domain.model.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthResponseDto {
    private String token;
    private String refreshToken;
    private User user;
}