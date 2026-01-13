package com.bescobar.notes.Friendship.application.port.in;

import com.bescobar.notes.Friendship.application.port.in.command.FriendDecisionCommand;
import com.bescobar.notes.Friendship.application.port.in.query.FriendshipDTO;

public interface RejectFriendRequest {

    FriendshipDTO reject(FriendDecisionCommand command);
}
