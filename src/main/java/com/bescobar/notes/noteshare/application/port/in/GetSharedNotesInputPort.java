package com.bescobar.notes.noteshare.application.port.in;

import com.bescobar.notes.noteshare.application.port.in.query.SharedNoteDTO;

import java.util.List;

/**
 * Use case for getting notes that have been shared with the current user.
 */
public interface GetSharedNotesInputPort {
    List<SharedNoteDTO> getNotesSharedWithMe(Long currentUserId);
}
