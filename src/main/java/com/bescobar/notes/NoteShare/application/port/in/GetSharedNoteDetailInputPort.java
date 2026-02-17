package com.bescobar.notes.NoteShare.application.port.in;

import com.bescobar.notes.NoteShare.application.port.in.query.SharedNoteDTO;

/**
 * Use case for getting details of a specific shared note.
 */
public interface GetSharedNoteDetailInputPort {
    SharedNoteDTO getSharedNoteDetail(Long currentUserId, Long sharedNoteId);
}
