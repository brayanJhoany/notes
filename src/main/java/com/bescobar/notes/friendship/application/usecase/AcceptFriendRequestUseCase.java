package com.bescobar.notes.friendship.application.usecase;

import org.springframework.stereotype.Service;

import com.bescobar.notes.friendship.application.port.in.AcceptFriendRequestInputPort;
import com.bescobar.notes.friendship.application.port.in.command.FriendDecisionCommand;
import com.bescobar.notes.friendship.application.port.in.query.FriendshipDTO;
import com.bescobar.notes.friendship.application.port.out.FriendshipRepositoryPort;
import com.bescobar.notes.friendship.domain.model.Friendship;
import com.bescobar.notes.friendship.domain.model.FriendshipStatus;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AcceptFriendRequestUseCase implements AcceptFriendRequestInputPort {

    private final FriendshipRepositoryPort friendshipOutPort;

    @Override
    public FriendshipDTO accept(FriendDecisionCommand command) {
        command.validate();

        Friendship friendship = friendshipOutPort.findById(command.getFriendshipId())
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found"));

        if (!friendship.getAddresseeId().equals(command.getUserId())) {
            throw new IllegalArgumentException("User is not the addressee of this request");
        }

        if (friendship.getFriendshipStatus() != FriendshipStatus.PENDING) {
            throw new IllegalArgumentException("Friend request is not pending");
        }

        friendship.changeStatus(FriendshipStatus.ACCEPTED);
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
