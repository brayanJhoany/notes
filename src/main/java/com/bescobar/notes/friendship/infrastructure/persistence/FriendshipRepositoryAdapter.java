package com.bescobar.notes.friendship.infrastructure.persistence;

import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.bescobar.notes.friendship.application.port.out.FriendshipRepositoryPort;
import com.bescobar.notes.friendship.domain.model.Friendship;
import com.bescobar.notes.friendship.domain.model.FriendshipStatus;
import com.bescobar.notes.friendship.infrastructure.persistence.entity.FriendshipEntity;
import com.bescobar.notes.friendship.infrastructure.persistence.mapper.FriendshipMapper;
import com.bescobar.notes.friendship.infrastructure.persistence.repository.FriendshipJpaRepository;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class FriendshipRepositoryAdapter implements FriendshipRepositoryPort {

    private final FriendshipJpaRepository friendshipJpaRepository;
    private final FriendshipMapper friendshipMapper;

    @Override
    public Friendship save(@NotNull Friendship friendship) {
        FriendshipEntity entity = friendshipMapper.toEntity(friendship);
        FriendshipEntity savedEntity = friendshipJpaRepository.save(entity);
        return friendshipMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Friendship> findById(@NotNull Long id) {
        return friendshipJpaRepository.findById(id)
                .map(friendshipMapper::toDomain);
    }

    @Override
    public boolean existsByStatusInBetweenUsers(@NotNull List<FriendshipStatus> statuses,
                                                @NotNull Long requesterId,
                                                @NotNull Long addresseeId) {
        return friendshipJpaRepository.existsByStatusInAndRequesterIdAndAddresseeIdOrStatusInAndRequesterIdAndAddresseeId(
                statuses,
                requesterId,
                addresseeId,
                statuses,
                addresseeId,
                requesterId);
    }

    @Override
    public Page<Friendship> findByStatusAndUserId(@NotNull Long userId,
                                                  @NotNull FriendshipStatus status,
                                                  @NotNull Pageable pageable) {
        return friendshipJpaRepository.findByStatusAndRequesterIdOrStatusAndAddresseeId(
                        status,
                        userId,
                        status,
                        userId,
                        pageable)
                .map(friendshipMapper::toDomain);
    }

    @Override
    public Friendship update(@NotNull Friendship friendship, @NotNull Long id) {
        FriendshipEntity entity = friendshipMapper.toEntity(friendship);
        entity.setId(id);
        FriendshipEntity updatedEntity = friendshipJpaRepository.save(entity);
        return friendshipMapper.toDomain(updatedEntity);
    }
}
