package com.bescobar.notes.note.application.port.in.command;

import lombok.Builder;
import lombok.Getter;

/**
 * Command for creating a new note. Commands represent intentions to modify the
 * system state. This is part of the application layer's input contract.
 */
@Getter
@Builder
public class CreateNoteCommand {

    private final String title;
    private final String content;
    private final Long userId;

    /**
     * Ensures command data complies with business rules before execution.
     */
    public void validate() {
        validateText(title, "title",3,255);
        validateText(content, "content",3,1000);
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
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
