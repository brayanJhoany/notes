package com.bescobar.notes.note.application.port.in.command;

import com.bescobar.notes.user.domain.model.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Command for updating an existing note.
 * Commands represent intentions to modify the system state.
 * This is part of the application layer's input contract.
 */
@Builder
@Getter
@Setter
public class UpdateNoteCommand {
    private String title;
    private String content;
    private User owner;

    /**
     * Ensures command data complies with business rules before execution.
     */
    public void validate() {
        validateText(title, "title", 3, 255);
        validateText(content, "content", 3, 1000);
        if (owner == null) {
            throw new IllegalArgumentException("owner must not be null");
        }
    }

    private static void validateText(String value, String fieldName, int min, int max) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        int length = value.trim().length();
        if (length < min) {
            throw new IllegalArgumentException(fieldName + " must have at least " + min + " characters");
        }
        if (length > max) {
            throw new IllegalArgumentException(fieldName + " must have at most " + max + " characters");
        }
    }
}
