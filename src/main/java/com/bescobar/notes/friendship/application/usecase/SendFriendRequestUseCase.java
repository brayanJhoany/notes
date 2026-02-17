package com.bescobar.notes.friendship.application.usecase;

import org.springframework.stereotype.Service;

import com.bescobar.notes.friendship.application.port.in.SendFriendRequestInputPort;
import com.bescobar.notes.friendship.application.port.in.command.SendFriendRequestCommand;
import com.bescobar.notes.friendship.application.port.in.query.FriendshipDTO;
import com.bescobar.notes.friendship.application.port.out.FriendshipRepositoryPort;
import com.bescobar.notes.friendship.domain.model.Friendship;
import com.bescobar.notes.friendship.domain.model.FriendshipStatus;

import lombok.AllArgsConstructor;
import java.util.List;

@Service
@AllArgsConstructor
public class SendFriendRequestUseCase implements SendFriendRequestInputPort {

    private final FriendshipRepositoryPort friendshipOutPort;

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
