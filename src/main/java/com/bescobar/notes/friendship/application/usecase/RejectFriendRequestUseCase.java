package com.bescobar.notes.friendship.application.usecase;

import org.springframework.stereotype.Service;

import com.bescobar.notes.friendship.application.port.in.RejectFriendRequestInputPort;
import com.bescobar.notes.friendship.application.port.in.command.FriendDecisionCommand;
import com.bescobar.notes.friendship.application.port.in.query.FriendshipDTO;
import com.bescobar.notes.friendship.application.port.out.FriendshipRepositoryOutputPort;
import com.bescobar.notes.friendship.domain.model.Friendship;
import com.bescobar.notes.friendship.domain.model.FriendshipStatus;

import lombok.AllArgsConstructor;

/**
 * Use case for rejecting a friend request.
 *
 * Business rules:
 * - Only the addressee can reject the request
 * - Request must be in PENDING status
 * - Changes status to REJECTED
 */
@Service
@AllArgsConstructor
public class RejectFriendRequestUseCase implements RejectFriendRequestInputPort {

    private final FriendshipRepositoryOutputPort friendshipOutPort;

    @Override
    public FriendshipDTO reject(FriendDecisionCommand command) {
        command.validate();

        Friendship friendship = friendshipOutPort.findById(command.getFriendshipId())
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found"));

        if (!friendship.getAddresseeId().equals(command.getUserId())) {
            throw new IllegalArgumentException("User is not the addressee of this request");
        }

        if (friendship.getFriendshipStatus() != FriendshipStatus.PENDING) {
            throw new IllegalArgumentException("Friend request is not pending");
        }

        friendship.changeStatus(FriendshipStatus.REJECTED);
        Friendship saved = friendshipOutPort.update(friendship, friendship.getId());

        return FriendshipDTO.builder()
                .id(saved.getId())
                .requesterId(saved.getRequesterId())
                .addresseeId(saved.getAddresseeId())
                .status(saved.getFriendshipStatus())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }
}
