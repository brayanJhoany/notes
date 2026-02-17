package com.bescobar.notes.noteshare.application.port.in;

/**
 * Use case for unsharing a note (removing the share).
 */
public interface UnshareNoteInputPort {
    void unshareNote(Long currentUserId, Long sharedNoteId);
}
