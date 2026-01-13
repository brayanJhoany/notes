package com.bescobar.notes.Friendship.infrastructure.web.mapper;

import org.springframework.stereotype.Component;

import com.bescobar.notes.Friendship.application.port.in.query.FriendshipDTO;
import com.bescobar.notes.Friendship.infrastructure.web.dto.FriendResponseDTO;

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
}
