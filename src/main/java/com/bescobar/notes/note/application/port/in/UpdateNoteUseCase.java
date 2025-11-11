package com.bescobar.notes.note.application.port.in;

import com.bescobar.notes.note.application.port.in.command.UpdateNoteCommand;
import com.bescobar.notes.note.application.port.in.query.NoteDTO;

public interface UpdateNoteUseCase {

    NoteDTO updateNote(UpdateNoteCommand updateNoteCommand , Long id);
}
