package com.bescobar.notes.user.application.port.in.query;

import com.bescobar.notes.user.domain.model.User;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification = "Intentional mutable object sharing in DTO")
public class AuthResponseDto {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private String refreshToken;
    private User user;
}
