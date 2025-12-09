package com.bescobar.notes.user.infrastructure.web.dto;

import com.bescobar.notes.user.infrastructure.persistence.entity.RoleEntity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = "password")
public class UserRequest {

    @NotBlank(message = "The full name is required")
    @Size(min = 3, max = 255)
    private String fullName;

    @Email(message = "The email must have a valid format, for example app@gmail.com")
    @NotBlank(message = "The email is required")
    private String email;

    @NotBlank(message = "The password is required")
    @Size(min=8, max=64, message="The password must be between 8 and 64 characters")
    private String password;

    @NotBlank(message = "The phone is required")
    private String phone;

    @NotBlank(message = "The address is required")
    private String address;

    @Enumerated(EnumType.STRING)
    private RoleEntity role;

}
