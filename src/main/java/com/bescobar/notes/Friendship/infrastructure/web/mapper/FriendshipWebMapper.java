package com.bescobar.notes.Friendship.infrastructure.web.mapper;

import org.springframework.stereotype.Component;

import com.bescobar.notes.Friendship.application.port.in.query.FriendshipDTO;
import com.bescobar.notes.Friendship.application.port.in.query.UserSummaryDTO;
import com.bescobar.notes.Friendship.infrastructure.web.dto.FriendResponseDTO;
import com.bescobar.notes.Friendship.infrastructure.web.dto.UserSummaryResponse;

@Component
public class FriendshipWebMapper {

    public FriendResponseDTO toWebResponse(FriendshipDTO dto) {
        if (dto == null) {
            return null;
        }

        return FriendResponseDTO.builder()
                .id(dto.getId())
                .requesterId(dto.getRequesterId())
                .addresseeId(dto.getAddresseeId())
                .status(dto.getStatus())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    public UserSummaryResponse toUserSummaryResponse(UserSummaryDTO dto) {

        if (dto == null) {
            return null;
        }

        return UserSummaryResponse.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .status(dto.getStatus())
                .direction(dto.getDirection())
                .build();
    }
}
