package com.bescobar.notes.note.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * HTTP/REST specific request DTO for creating notes.
 * This is part of the infrastructure layer (web adapter).
 * Contains REST-specific validations and formatting.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NoteWebRequest {

    @NotBlank(message = "The title is required.")
    @Size(min = 3, max = 500, message = "The title must be between 3 and 500 characters long.")
    private String title;

    @NotBlank(message = "The content is required.")
    @Size(min = 3, max = 5000, message = "The content must be between 3 and 5000 characters long.")
    private String content;
}
