package com.bescobar.notes.Friendship.domain.model;

import java.time.LocalDateTime;

public class Friendship {

    private FriendshipStatus friendshipStatus;
    private Long id;
    private Long requesterId;
    private Long addresseeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Friendship() {
        this.friendshipStatus = FriendshipStatus.PENDING;
    }

    public Friendship(Long id, Long requesterId, Long addresseeId, FriendshipStatus friendshipStatus) {
        this.id = id;
        this.requesterId = requesterId;
        this.addresseeId = addresseeId;
        this.friendshipStatus = friendshipStatus;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Friendship(Long id, Long requesterId, Long addresseeId, FriendshipStatus friendshipStatus,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.requesterId = requesterId;
        this.addresseeId = addresseeId;
        this.friendshipStatus = friendshipStatus;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getRequesterId() {
        return requesterId;
    }

    public Long getAddresseeId() {
        return addresseeId;
    }

    public FriendshipStatus getFriendshipStatus() {
        return friendshipStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void changeStatus(FriendshipStatus newStatus) {
        if (newStatus == null || newStatus == this.friendshipStatus) {
            return;
        }
        this.friendshipStatus = newStatus;
        touch();
    }

    public void updateRequesterId(Long requesterId) {
        if (requesterId == null || requesterId.equals(this.requesterId)) {
            return;
        }
        this.requesterId = requesterId;
        touch();
    }

    public void updateAddresseeId(Long addresseeId) {
        if (addresseeId == null || addresseeId.equals(this.addresseeId)) {
            return;
        }
        this.addresseeId = addresseeId;
        touch();
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }
}
