package com.bescobar.notes.noteshare.infrastructure.web.mapper;

import com.bescobar.notes.noteshare.application.port.in.command.ShareNoteCommand;
import com.bescobar.notes.noteshare.application.port.in.query.SharedNoteDTO;
import com.bescobar.notes.noteshare.infrastructure.web.dto.ShareNoteRequest;
import com.bescobar.notes.noteshare.infrastructure.web.dto.SharedNoteResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper between web DTOs and application DTOs.
 */
@Component
public class NoteShareWebMapper {

    /**
     * Converts web request to application command.
     */
    public ShareNoteCommand toCommand(ShareNoteRequest request) {
        return ShareNoteCommand.builder()
                .noteId(request.getNoteId())
                .sharedWithUserId(request.getSharedWithUserId())
                .permission(request.getPermission())
                .build();
    }

    /**
     * Converts application DTO to web response.
     */
    public SharedNoteResponse toResponse(SharedNoteDTO dto) {
        return SharedNoteResponse.builder()
                .id(dto.getId())
                .noteId(dto.getNoteId())
                .noteTitle(dto.getNoteTitle())
                .noteContent(dto.getNoteContent())
                .sharedByUserId(dto.getSharedByUserId())
                .sharedByUserName(dto.getSharedByUserName())
                .sharedWithUserId(dto.getSharedWithUserId())
                .sharedWithUserName(dto.getSharedWithUserName())
                .permission(dto.getPermission())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    /**
     * Converts list of application DTOs to list of web responses.
     */
    public List<SharedNoteResponse> toResponseList(List<SharedNoteDTO> dtos) {
        return dtos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
