package com.bescobar.notes.Friendship.application.port.in;

import com.bescobar.notes.Friendship.application.port.in.command.BlockFriendRequestCommand;
import com.bescobar.notes.Friendship.application.port.in.query.FriendshipDTO;

public interface BlockFriendRequest {

    FriendshipDTO block(BlockFriendRequestCommand command);

}
