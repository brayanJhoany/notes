package com.bescobar.notes.friendship.application.usecase;

import org.springframework.stereotype.Service;

import com.bescobar.notes.friendship.application.port.in.SendFriendRequestInputPort;
import com.bescobar.notes.friendship.application.port.in.command.SendFriendRequestCommand;
import com.bescobar.notes.friendship.application.port.in.query.FriendshipDTO;
import com.bescobar.notes.friendship.application.port.out.FriendshipRepositoryOutputPort;
import com.bescobar.notes.friendship.domain.model.Friendship;
import com.bescobar.notes.friendship.domain.model.FriendshipStatus;

import lombok.AllArgsConstructor;
import java.util.List;

/**
 * Use case for sending a friend request.
 *
 * Business rules:
 * - Cannot send a request if one already exists (PENDING or ACCEPTED status)
 * - Creates a new friendship with PENDING status
 */
@Service
@AllArgsConstructor
public class SendFriendRequestUseCase implements SendFriendRequestInputPort {

    private final FriendshipRepositoryOutputPort friendshipOutPort;

    @Override
    public FriendshipDTO send(SendFriendRequestCommand command) {
        command.validate();

        List<FriendshipStatus> blockedStatuses = List.of(FriendshipStatus.PENDING, FriendshipStatus.ACCEPTED);
        boolean exists = friendshipOutPort.existsByStatusInBetweenUsers(
                blockedStatuses,
                command.getRequesterId(),
                command.getAddresseeId());
        if (exists) {
            throw new IllegalArgumentException("Friend request already exists");
        }

        Friendship friendship = new Friendship(
                null,
                command.getRequesterId(),
                command.getAddresseeId(),
                FriendshipStatus.PENDING);

        Friendship saved = friendshipOutPort.save(friendship);

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
