package com.bescobar.notes.friendship.application.port.out;

import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bescobar.notes.friendship.domain.model.Friendship;
import com.bescobar.notes.friendship.domain.model.FriendshipStatus;

public interface FriendshipRepositoryOutputPort {
    Friendship save(Friendship friendship);
    Optional<Friendship> findById(Long id);
    boolean existsByStatusInBetweenUsers(List<FriendshipStatus> statuses, Long requesterId, Long addresseeId);
    Page<Friendship> findByStatusAndUserId(Long userId, FriendshipStatus status, Pageable pageable);
    Friendship update(Friendship friendship, Long id);
}
