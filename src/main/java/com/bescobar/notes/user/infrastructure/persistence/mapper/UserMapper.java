package com.bescobar.notes.user.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import com.bescobar.notes.user.domain.model.Role;
import com.bescobar.notes.user.domain.model.User;
import com.bescobar.notes.user.infrastructure.persistence.entity.RoleEntity;
import com.bescobar.notes.user.infrastructure.persistence.entity.UserEntity;

@Component
public class UserMapper {

    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setFullName(user.getFullName());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setPhone(user.getPhone());
        entity.setAddress(user.getAddress());
        entity.setRole(mapToEntityRole(user.getRole()));
        entity.setActive(user.getActive());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());

        return entity;
    }

    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return new User(
            entity.getId(),
            entity.getUsername(),
            entity.getFullName(),
            entity.getEmail(),
            entity.getPassword(),
            entity.getPhone(),
            entity.getAddress(),
            mapToDomainRole(entity.getRole()),
            entity.getActive(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    private RoleEntity mapToEntityRole(
            Role domainRole) {
        if (domainRole == null) {
            return RoleEntity.REGULAR;
        }

        return switch (domainRole) {
            case ADMIN -> RoleEntity.ADMIN;
            case REGULAR -> RoleEntity.REGULAR;
        };
    }

    private Role mapToDomainRole(
            RoleEntity entityRole) {
        if (entityRole == null) {
            return Role.REGULAR;
        }

        return switch (entityRole) {
            case ADMIN -> Role.ADMIN;
            case REGULAR -> Role.REGULAR;
        };
    }
}