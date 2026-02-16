package com.bescobar.notes.Friendship.infrastructure.web.dto;

import java.time.LocalDateTime;

import com.bescobar.notes.Friendship.domain.model.FriendshipStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendResponseDTO {
    private Long id;
    private Long requesterId;
    private Long addresseeId;
    private FriendshipStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
