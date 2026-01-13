package com.bescobar.notes.Friendship.application.port.in;

import com.bescobar.notes.Friendship.application.port.in.command.AcceptFriendRequestCommand;
import com.bescobar.notes.Friendship.application.port.in.query.FriendshipDTO;

public interface AcceptFriendRequest {
    FriendshipDTO accept(AcceptFriendRequestCommand command);
}
