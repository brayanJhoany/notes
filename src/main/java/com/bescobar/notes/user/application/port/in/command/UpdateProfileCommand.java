package com.bescobar.notes.user.application.port.in.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
