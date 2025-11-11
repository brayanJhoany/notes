package com.bescobar.notes.note.infrastructure.web.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.bescobar.notes.note.application.port.in.query.NoteDTO;
import com.bescobar.notes.note.infrastructure.web.dto.NoteWebResponse;

/**
 * Mapper for converting between Application DTOs and Web DTOs.
 * This is part of the web adapter (infrastructure layer).
 * Keeps the controller clean by centralizing mapping logic.
 */
@Component
public class NoteWebMapper {

    /**
     * Converts a single NoteDTO to NoteWebResponse
     */
    public NoteWebResponse toWebResponse(NoteDTO dto) {
        if (dto == null) return null;

        return NoteWebResponse.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .createdAt(dto.getCreatedAt())
                .userId(dto.getUserId())
                .build();
    }

    /**
     * Converts a list of NoteDTOs to a list of NoteWebResponses
     */
    public List<NoteWebResponse> toWebResponseList(List<NoteDTO> listNoteDto) {
        if (listNoteDto == null) return List.of();

        return listNoteDto.stream()
                .map(this::toWebResponse)
                .collect(Collectors.toList());
    }
}
