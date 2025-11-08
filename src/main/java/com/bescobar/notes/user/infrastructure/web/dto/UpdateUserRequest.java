package com.bescobar.notes.user.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateUserRequest {

    @NotBlank(message = "The username is required.")
    @Size(min = 3, max = 255)
    private String username;

    @NotBlank(message = "The full name is required")
    @Size(min = 3, max = 255)
    private String fullName;

    @Email(message = "The email must have a valid format, for example app@gmail.com")
    @NotBlank(message = "The email is required")
    private String email;

    @NotBlank(message = "The phone is required")
    private String phone;

    @NotBlank(message = "The address is required")
    private String address;
}