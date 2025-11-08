package com.bescobar.notes.note.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * HTTP/REST specific response DTO for notes.
 * This is part of the infrastructure layer (web adapter).
 * Represents how note data is exposed via REST API.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteWebResponse {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private Long userId;
}
