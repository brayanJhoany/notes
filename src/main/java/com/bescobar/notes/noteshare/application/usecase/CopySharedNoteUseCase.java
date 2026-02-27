package com.bescobar.notes.noteshare.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bescobar.notes.noteshare.application.port.in.CopySharedNoteInputPort;
import com.bescobar.notes.noteshare.application.port.in.command.CopySharedNoteCommand;
import com.bescobar.notes.noteshare.application.port.out.NoteShareRepositoryOutputPort;
import com.bescobar.notes.noteshare.domain.exception.NoteNotFoundException;
import com.bescobar.notes.noteshare.domain.exception.NoteShareBusinessRuleException;
import com.bescobar.notes.noteshare.domain.exception.SharedNoteNotFoundException;
import com.bescobar.notes.noteshare.domain.model.SharedNote;
import com.bescobar.notes.note.application.port.out.NoteRepositoryOutputPort;
import com.bescobar.notes.note.domain.model.Note;
import com.bescobar.notes.user.application.port.out.UserRepositoryOutputPort;
import com.bescobar.notes.user.domain.model.User;

import lombok.AllArgsConstructor;

/**
 * Use case for copying a shared note (copy-on-write pattern).
 *
 * Business rules:
 * 1. Only the recipient (sharedWithUserId) can copy
 * 2. The shared note must have CAN_COPY permission
 * 3. Creates an independent copy owned by the current user
 */
@Service
@AllArgsConstructor
public class CopySharedNoteUseCase implements CopySharedNoteInputPort {

    private final NoteShareRepositoryOutputPort noteShareRepository;
    private final NoteRepositoryOutputPort noteRepository;
    private final UserRepositoryOutputPort userRepository;

    @Override
    @Transactional
    public Note copySharedNote(Long currentUserId, CopySharedNoteCommand command) {
        SharedNote sharedNote = noteShareRepository.findById(command.getSharedNoteId())
                .orElseThrow(() -> new SharedNoteNotFoundException(command.getSharedNoteId()));

        if (!sharedNote.isSharedWith(currentUserId)) {
            throw new NoteShareBusinessRuleException("You can only copy notes shared with you");
        }

        if (!sharedNote.canCopy()) {
            throw new NoteShareBusinessRuleException("This note does not have copy permission");
        }

        Note originalNote = noteRepository.findById(sharedNote.getNoteId())
                .orElseThrow(() -> new NoteNotFoundException("Original note not found"));

        User currentUser = userRepository.findById(currentUserId);

        Note copiedNote = new Note(
                null,
                originalNote.getTitle() + " (Copy)",
                originalNote.getContent(),
                currentUser
        );

        return noteRepository.save(copiedNote);
    }
}
