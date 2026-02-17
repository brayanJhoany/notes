package com.bescobar.notes.friendship.application.port.in;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bescobar.notes.friendship.application.port.in.query.UserSummaryDTO;
import com.bescobar.notes.friendship.domain.model.FriendshipStatus;

public interface GetFriendRequestsByStatus {

    Page<UserSummaryDTO> getFriendRequests(Long userId, FriendshipStatus status, Pageable pageable);
}
