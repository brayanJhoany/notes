package com.bescobar.notes.Friendship.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import com.bescobar.notes.Friendship.domain.model.Friendship;
import com.bescobar.notes.Friendship.infrastructure.persistence.entity.FriendshipEntity;

import jakarta.validation.constraints.NotNull;

@Component
public class FriendshipMapper {

    public FriendshipEntity toEntity(@NotNull Friendship friendship) {
        FriendshipEntity entity = new FriendshipEntity();
        entity.setId(friendship.getId());
        entity.setRequesterId(friendship.getRequesterId());
        entity.setAddresseeId(friendship.getAddresseeId());
        entity.setStatus(friendship.getFriendshipStatus());
        entity.setCreatedAt(friendship.getCreatedAt());
        entity.setUpdatedAt(friendship.getUpdatedAt());
        return entity;
    }

    public Friendship toDomain(@NotNull FriendshipEntity entity) {
        return new Friendship(
                entity.getId(),
                entity.getRequesterId(),
                entity.getAddresseeId(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
