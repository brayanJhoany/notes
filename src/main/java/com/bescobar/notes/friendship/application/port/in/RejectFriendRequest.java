package com.bescobar.notes.friendship.application.port.in;

import com.bescobar.notes.friendship.application.port.in.command.FriendDecisionCommand;
import com.bescobar.notes.friendship.application.port.in.query.FriendshipDTO;

public interface RejectFriendRequest {

    FriendshipDTO reject(FriendDecisionCommand command);
}
