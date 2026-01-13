package com.bescobar.notes.Friendship.application.port.in;

import com.bescobar.notes.Friendship.application.port.in.command.SendFriendRequestCommand;
import com.bescobar.notes.Friendship.application.port.in.query.FriendshipDTO;

public interface SendFriendRequest {

    FriendshipDTO send(SendFriendRequestCommand command);
}
