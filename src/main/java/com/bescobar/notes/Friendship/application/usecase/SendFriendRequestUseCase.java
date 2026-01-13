package com.bescobar.notes.Friendship.application.usecase;

import org.springframework.stereotype.Service;

import com.bescobar.notes.Friendship.application.port.in.SendFriendRequest;
import com.bescobar.notes.Friendship.application.port.in.command.SendFriendRequestCommand;
import com.bescobar.notes.Friendship.application.port.in.query.FriendshipDTO;
import com.bescobar.notes.Friendship.application.port.out.FriendshipOutPort;
import com.bescobar.notes.Friendship.domain.model.Friendship;
import com.bescobar.notes.Friendship.domain.model.FriendshipStatus;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SendFriendRequestUseCase implements SendFriendRequest {

    private final FriendshipOutPort friendshipOutPort;

    @Override
    public FriendshipDTO send(SendFriendRequestCommand command) {
        command.validate();

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
