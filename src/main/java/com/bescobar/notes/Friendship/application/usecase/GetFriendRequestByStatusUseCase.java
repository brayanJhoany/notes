package com.bescobar.notes.Friendship.application.usecase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bescobar.notes.Friendship.application.port.in.GetFriendRequestsByStatus;
import com.bescobar.notes.Friendship.application.port.in.query.UserSummaryDTO;
import com.bescobar.notes.Friendship.application.port.out.FriendshipOutPort;
import com.bescobar.notes.Friendship.domain.model.Friendship;
import com.bescobar.notes.Friendship.domain.model.FriendshipStatus;
import com.bescobar.notes.user.application.port.out.UserRepositoryPort;
import com.bescobar.notes.user.domain.model.User;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GetFriendRequestByStatusUseCase implements GetFriendRequestsByStatus{

    private final FriendshipOutPort friendshipOutPort;
    private final UserRepositoryPort userRepositoryPort;

    @Override
    public Page<UserSummaryDTO> getFriendRequests(Long userId, FriendshipStatus status, Pageable pageable) {
        Page<Friendship> friendships = friendshipOutPort.findByStatusAndUserId(userId, status, pageable);
        return friendships.map(friendship -> toUserSummary(friendship, userId));
    }

    private UserSummaryDTO toUserSummary(Friendship friendship, Long userId) {
        Long otherUserId = friendship.getRequesterId().equals(userId)
                ? friendship.getAddresseeId()
                : friendship.getRequesterId();

        User user = userRepositoryPort.findById(otherUserId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with id: " + otherUserId);
        }

        return UserSummaryDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .status(friendship.getFriendshipStatus())
                .build();
    }
    
}
