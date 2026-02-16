package com.bescobar.notes.Friendship.infrastructure.web.dto;

import com.bescobar.notes.Friendship.domain.model.FriendshipStatus;
import com.bescobar.notes.Friendship.application.port.in.query.FriendRequestDirection;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserSummaryResponse {

    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private FriendshipStatus status;
    private FriendRequestDirection direction;
}
