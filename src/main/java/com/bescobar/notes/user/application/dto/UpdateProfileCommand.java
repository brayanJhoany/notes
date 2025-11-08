package com.bescobar.notes.user.application.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProfileCommand {
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String address;
}