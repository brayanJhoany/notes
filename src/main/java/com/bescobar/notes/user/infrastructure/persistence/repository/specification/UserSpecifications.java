package com.bescobar.notes.user.infrastructure.persistence.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import com.bescobar.notes.user.infrastructure.persistence.entity.UserEntity;

public final class UserSpecifications {

    private UserSpecifications() {
    }

    public static Specification<UserEntity> emailContains(String email) {
        return (root, query, cb)
                -> (email == null || email.isBlank())
                ? null
                : cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<UserEntity> fullnameContains(String fullname) {
        return (root, query, cb)
                -> (fullname == null || fullname.isBlank())
                ? null
                : cb.like(cb.lower(root.get("fullName")), "%" + fullname.toLowerCase() + "%");
    }

    public static Specification<UserEntity> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("active"));
    }
}
