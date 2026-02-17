package com.bescobar.notes.user.infrastructure.web.mapper;

import com.bescobar.notes.user.application.port.in.command.LoginCommand;
import com.bescobar.notes.user.application.port.in.command.UpdateProfileCommand;
import com.bescobar.notes.user.domain.model.Role;
import com.bescobar.notes.user.domain.model.User;
import com.bescobar.notes.user.infrastructure.persistence.entity.RoleEntity;
import com.bescobar.notes.user.infrastructure.web.dto.AuthRequest;
import com.bescobar.notes.user.infrastructure.web.dto.UpdateUserRequest;
import com.bescobar.notes.user.infrastructure.web.dto.UserRequest;
import com.bescobar.notes.user.infrastructure.web.dto.UserResponse;

public class UserDtoMapper {

    public static User toDomain(UserRequest request) {
        if (request == null) {
            return null;
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setRole(mapToDomainRole(request.getRole()));

        return user;
    }

    public static UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .role(mapToEntityRole(user.getRole()))
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public static UpdateProfileCommand toUpdateProfileCommand(UpdateUserRequest request){
        if (request == null) {
            return null;
        }
        UpdateProfileCommand response = UpdateProfileCommand.builder().build();
        response.setFullName(request.getFullName());
        response.setEmail(request.getEmail());
        response.setPhone(request.getPhone());
        response.setAddress(request.getAddress());

        return response;
    }

    public static LoginCommand toLoginCommand(AuthRequest request){
        if (request == null) {
            return null;
        }
        LoginCommand loginCommand = new LoginCommand(
                request.getEmail(),
                request.getPassword()
        );
        return loginCommand;
    }
    private static Role mapToDomainRole(RoleEntity roleEntity) {
        if (roleEntity == null) {
            return Role.REGULAR;
        }

        return switch (roleEntity) {
            case ADMIN -> Role.ADMIN;
            case REGULAR -> Role.REGULAR;
        };
    }

    private static RoleEntity mapToEntityRole(Role domainRole) {
        if (domainRole == null) {
            return RoleEntity.REGULAR;
        }

        return switch (domainRole) {
            case ADMIN -> RoleEntity.ADMIN;
            case REGULAR -> RoleEntity.REGULAR;
        };
    }



}
