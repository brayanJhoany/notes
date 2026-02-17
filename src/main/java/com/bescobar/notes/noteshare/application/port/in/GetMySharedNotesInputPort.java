package com.bescobar.notes.noteshare.application.port.in;

import com.bescobar.notes.noteshare.application.port.in.query.SharedNoteDTO;

import java.util.List;

/**
 * Use case for getting notes that the current user has shared with others.
 */
public interface GetMySharedNotesInputPort {
    List<SharedNoteDTO> getNotesSharedByMe(Long currentUserId);
}
