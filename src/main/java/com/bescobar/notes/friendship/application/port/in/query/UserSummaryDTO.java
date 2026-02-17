package com.bescobar.notes.friendship.application.port.in.query;

import com.bescobar.notes.friendship.domain.model.FriendshipStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryDTO {

    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private FriendshipStatus status;
    private FriendRequestDirection direction;

}
