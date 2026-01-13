package com.bescobar.notes.Friendship.application.port.in.query;

import java.time.LocalDateTime;

import com.bescobar.notes.Friendship.domain.model.FriendshipStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipDTO {
    private Long id;
    private Long requesterId;
    private Long addresseeId;
    private FriendshipStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
