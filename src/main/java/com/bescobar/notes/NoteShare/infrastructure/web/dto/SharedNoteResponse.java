package com.bescobar.notes.NoteShare.infrastructure.web.dto;

import com.bescobar.notes.NoteShare.domain.model.SharePermission;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Web response DTO for shared notes.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedNoteResponse {
    private Long id;
    private Long noteId;
    private String noteTitle;
    private String noteContent;
    private Long sharedByUserId;
    private String sharedByUserName;
    private Long sharedWithUserId;
    private String sharedWithUserName;
    private SharePermission permission;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
