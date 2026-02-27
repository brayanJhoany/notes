package com.bescobar.notes.noteshare.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bescobar.notes.noteshare.application.port.in.UnshareNoteInputPort;
import com.bescobar.notes.noteshare.application.port.out.NoteShareRepositoryOutputPort;
import com.bescobar.notes.noteshare.domain.exception.NoteShareBusinessRuleException;
import com.bescobar.notes.noteshare.domain.exception.SharedNoteNotFoundException;
import com.bescobar.notes.noteshare.domain.model.SharedNote;

import lombok.AllArgsConstructor;

/**
 * Use case for unsharing a note (removing the share).
 *
 * Business rules:
 * 1. Only the owner (sharedByUserId) can unshare
 */
@Service
@AllArgsConstructor
public class UnshareNoteUseCase implements UnshareNoteInputPort {

    private final NoteShareRepositoryOutputPort noteShareRepository;

    @Override
    @Transactional
    public void unshareNote(Long currentUserId, Long sharedNoteId) {
        SharedNote sharedNote = noteShareRepository.findById(sharedNoteId)
                .orElseThrow(() -> new SharedNoteNotFoundException(sharedNoteId));

        if (!sharedNote.isSharedBy(currentUserId)) {
            throw new NoteShareBusinessRuleException("You can only unshare notes you have shared");
        }

        noteShareRepository.deleteById(sharedNoteId);
    }
}
