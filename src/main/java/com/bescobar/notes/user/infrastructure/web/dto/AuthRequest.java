package com.bescobar.notes.user.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = "password")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthRequest {

    @NotBlank()
    @Email()
    private String email;

    @NotBlank()
    @Size(min = 6, max = 255)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
