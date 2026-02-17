package com.bescobar.notes.friendship.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bescobar.notes.friendship.domain.model.FriendshipStatus;
import com.bescobar.notes.friendship.infrastructure.persistence.entity.FriendshipEntity;

public interface FriendshipJpaRepository extends JpaRepository<FriendshipEntity, Long> {
    Page<FriendshipEntity> findByStatusAndRequesterIdOrStatusAndAddresseeId(
            FriendshipStatus statusForRequester,
            Long requesterId,
            FriendshipStatus statusForAddressee,
            Long addresseeId,
            Pageable pageable);

    boolean existsByStatusInAndRequesterIdAndAddresseeIdOrStatusInAndRequesterIdAndAddresseeId(
            List<FriendshipStatus> statusesForRequester,
            Long requesterId,
            Long addresseeId,
            List<FriendshipStatus> statusesForAddressee,
            Long requesterIdInverse,
            Long addresseeIdInverse);
}
