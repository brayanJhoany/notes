package com.bescobar.notes.Friendship.application.usecase;

import org.springframework.stereotype.Service;

import com.bescobar.notes.Friendship.application.port.in.RejectFriendRequest;
import com.bescobar.notes.Friendship.application.port.in.command.FriendDecisionCommand;
import com.bescobar.notes.Friendship.application.port.in.query.FriendshipDTO;
import com.bescobar.notes.Friendship.application.port.out.FriendshipOutPort;
import com.bescobar.notes.Friendship.domain.model.Friendship;
import com.bescobar.notes.Friendship.domain.model.FriendshipStatus;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RejectFriendRequestUseCase implements RejectFriendRequest {

    private final FriendshipOutPort friendshipOutPort;

    @Override
    public FriendshipDTO reject(FriendDecisionCommand command) {
        command.validate();

        Friendship friendship = friendshipOutPort.findById(command.getFriendshipId())
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found"));

        if (!friendship.getAddresseeId().equals(command.getAddresseeId())) {
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
