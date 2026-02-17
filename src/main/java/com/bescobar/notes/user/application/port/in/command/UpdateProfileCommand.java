package com.bescobar.notes.user.application.port.in.command;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProfileCommand {
    private String fullName;
    private String email;
    private String phone;
    private String address;
}
