package com.bescobar.notes.user.infrastructure.web.dto;

import java.time.LocalDateTime;

import com.bescobar.notes.user.infrastructure.persistence.entity.RoleEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@Builder
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private RoleEntity role = RoleEntity.REGULAR;
    private Boolean active = true;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    public static UserResponse copyOf(UserResponse source) {
        if (source == null) {
            return null;
        }

        return UserResponse.builder()
                .id(source.getId())
                .fullName(source.getFullName())
                .email(source.getEmail())
                .phone(source.getPhone())
                .address(source.getAddress())
                .role(source.getRole())
                .active(source.getActive())
                .createdAt(source.getCreatedAt())
                .updatedAt(source.getUpdatedAt())
                .build();
    }
}
