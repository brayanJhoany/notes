package com.bescobar.notes.user.infrastructure.web.dto;

import com.bescobar.notes.user.infrastructure.persistence.entity.RoleEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private RoleEntity role = RoleEntity.REGULAR;
    private Boolean active = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
