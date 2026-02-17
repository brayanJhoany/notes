package com.bescobar.notes.noteshare.infrastructure.web.dto;

import com.bescobar.notes.noteshare.domain.model.SharePermission;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Web request DTO for sharing a note.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShareNoteRequest {

    @NotNull(message = "Note ID is required")
    private Long noteId;

    @NotNull(message = "Shared with user ID is required")
    private Long sharedWithUserId;

    @NotNull(message = "Permission is required")
    private SharePermission permission;
}
