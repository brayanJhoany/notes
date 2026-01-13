package com.bescobar.notes.Friendship.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bescobar.notes.Friendship.infrastructure.persistence.entity.FriendshipEntity;

public interface FriendshipJpaRepository extends JpaRepository<FriendshipEntity, Long> {
}
