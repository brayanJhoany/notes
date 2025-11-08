package com.bescobar.notes.note.application.port.in.command;

import lombok.Builder;
import lombok.Getter;

/**
 * Command for creating a new note.
 * Commands represent intentions to modify the system state.
 * This is part of the application layer's input contract.
 */
@Builder
@Getter
public class CreateNoteCommand {
    private String title;
    private String content;
    private Long userId;
}
