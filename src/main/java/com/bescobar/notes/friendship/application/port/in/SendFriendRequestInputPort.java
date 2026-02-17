package com.bescobar.notes.friendship.application.port.in;

import com.bescobar.notes.friendship.application.port.in.command.SendFriendRequestCommand;
import com.bescobar.notes.friendship.application.port.in.query.FriendshipDTO;

public interface SendFriendRequestInputPort {

    FriendshipDTO send(SendFriendRequestCommand command);
}
