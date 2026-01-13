package com.bescobar.notes.Friendship.application.port.in.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FriendDecisionCommand {

    private final Long friendshipId;
    private final Long addresseeId;

    public void validate() {
        if (friendshipId == null) {
            throw new IllegalArgumentException("friendshipId must not be null");
        }
        if (addresseeId == null) {
            throw new IllegalArgumentException("addresseeId must not be null");
        }
    }
}
