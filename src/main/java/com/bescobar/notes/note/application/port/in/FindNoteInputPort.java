package com.bescobar.notes.note.application.port.in;

import com.bescobar.notes.note.application.port.in.query.NoteDTO;
import com.bescobar.notes.user.domain.model.User;

public interface FindNoteInputPort {

    NoteDTO findById(User owner, Long id);

}
