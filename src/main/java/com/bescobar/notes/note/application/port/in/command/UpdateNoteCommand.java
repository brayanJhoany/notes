package com.bescobar.notes.note.application.port.in.command;

import com.bescobar.notes.user.domain.model.User;
import lombok.Builder;
import lombok.Getter;

/**
 * Command for updating an existing note.
 * Commands represent intentions to modify the system state.
 * This is part of the application layer's input contract.
 */
@Builder
@Getter
public class UpdateNoteCommand {
    private String title;
    private String content;
    private User owner;
}
