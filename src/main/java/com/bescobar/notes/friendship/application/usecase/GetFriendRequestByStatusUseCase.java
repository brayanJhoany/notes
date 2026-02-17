package com.bescobar.notes.friendship.application.usecase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bescobar.notes.friendship.application.port.in.GetFriendRequestsByStatusInputPort;
import com.bescobar.notes.friendship.application.port.in.query.FriendRequestDirection;
import com.bescobar.notes.friendship.application.port.in.query.UserSummaryDTO;
import com.bescobar.notes.friendship.application.port.out.FriendshipRepositoryPort;
import com.bescobar.notes.friendship.domain.model.Friendship;
import com.bescobar.notes.friendship.domain.model.FriendshipStatus;
import com.bescobar.notes.user.application.port.out.UserRepositoryPort;
import com.bescobar.notes.user.domain.model.User;

import lombok.AllArgsConstructor;

/**
 * Use case for retrieving friend requests by status.
 *
 * Returns a paginated list of users with friendship information,
 * including the direction (INCOMING/OUTGOING) based on the current user.
 */
@Service
@AllArgsConstructor
public class GetFriendRequestByStatusUseCase implements GetFriendRequestsByStatusInputPort{

    private final FriendshipRepositoryPort friendshipOutPort;
    private final UserRepositoryPort userRepositoryPort;

    @Override
    public Page<UserSummaryDTO> getFriendRequests(Long userId, FriendshipStatus status, Pageable pageable) {
        Page<Friendship> friendships = friendshipOutPort.findByStatusAndUserId(userId, status, pageable);
        return friendships.map(friendship -> toUserSummary(friendship, userId));
    }

    private UserSummaryDTO toUserSummary(Friendship friendship, Long userId) {
        boolean isRequester = friendship.getRequesterId().equals(userId);
        Long otherUserId = isRequester
                ? friendship.getAddresseeId()
                : friendship.getRequesterId();

        User user = userRepositoryPort.findById(otherUserId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with id: " + otherUserId);
        }

        return UserSummaryDTO.builder()
                .id(friendship.getId())
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .status(friendship.getFriendshipStatus())
                .direction(isRequester ? FriendRequestDirection.OUTGOING : FriendRequestDirection.INCOMING)
                .build();
    }
    
}
