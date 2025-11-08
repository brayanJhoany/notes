package com.bescobar.notes.note.application.port.in.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for note queries/responses.
 * This represents the application layer's output contract.
 * Used for reading note data without modifying state.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private Long userId;
}
