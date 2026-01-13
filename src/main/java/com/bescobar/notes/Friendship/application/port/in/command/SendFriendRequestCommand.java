package com.bescobar.notes.Friendship.application.port.in.command;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SendFriendRequestCommand {

    private final Long requesterId;
    private final Long addresseeId;

    public void validate() {
        if (requesterId == null) {
            throw new IllegalArgumentException("requesterId must not be null");
        }
        if (addresseeId == null) {
            throw new IllegalArgumentException("addresseeId must not be null");
        }
        if (requesterId.equals(addresseeId)) {
            throw new IllegalArgumentException("requesterId must be different from addresseeId");
        }
    }
}
