package com.bescobar.notes.NoteShare.application.usecase;

import org.springframework.stereotype.Service;

import com.bescobar.notes.NoteShare.application.port.in.GetSharedNoteDetailInputPort;
import com.bescobar.notes.NoteShare.application.port.in.query.SharedNoteDTO;
import com.bescobar.notes.NoteShare.application.port.out.NoteShareRepositoryPort;
import com.bescobar.notes.NoteShare.domain.exception.NoteShareBusinessRuleException;
import com.bescobar.notes.NoteShare.domain.exception.SharedNoteNotFoundException;
import com.bescobar.notes.NoteShare.domain.model.SharedNote;
import com.bescobar.notes.note.application.port.out.NoteRepositoryPort;
import com.bescobar.notes.note.domain.model.Note;
import com.bescobar.notes.user.application.port.out.UserRepositoryPort;
import com.bescobar.notes.user.domain.model.User;

import lombok.AllArgsConstructor;

/**
 * Use case for getting details of a specific shared note.
 */
@Service
@AllArgsConstructor
public class GetSharedNoteDetailUseCase implements GetSharedNoteDetailInputPort {

    private final NoteShareRepositoryPort noteShareRepository;
    private final NoteRepositoryPort noteRepository;
    private final UserRepositoryPort userRepository;

    @Override
    public SharedNoteDTO getSharedNoteDetail(Long currentUserId, Long sharedNoteId) {
        SharedNote sharedNote = noteShareRepository.findById(sharedNoteId)
                .orElseThrow(() -> new SharedNoteNotFoundException(sharedNoteId));

        if (!sharedNote.isSharedBy(currentUserId) && !sharedNote.isSharedWith(currentUserId)) {
            throw new NoteShareBusinessRuleException("You don't have permission to view this shared note");
        }

        return toDTO(sharedNote);
    }

    private SharedNoteDTO toDTO(SharedNote sharedNote) {
        Note note = noteRepository.findById(sharedNote.getNoteId()).orElse(null);
        User sharedByUser = userRepository.findById(sharedNote.getSharedByUserId());
        User sharedWithUser = userRepository.findById(sharedNote.getSharedWithUserId());

        return SharedNoteDTO.builder()
                .id(sharedNote.getId())
                .noteId(sharedNote.getNoteId())
                .noteTitle(note != null ? note.getTitle() : "Deleted Note")
                .noteContent(note != null ? note.getContent() : "")
                .sharedByUserId(sharedNote.getSharedByUserId())
                .sharedByUserName(sharedByUser != null ? sharedByUser.getFullName() : "Unknown User")
                .sharedWithUserId(sharedNote.getSharedWithUserId())
                .sharedWithUserName(sharedWithUser != null ? sharedWithUser.getFullName() : "Unknown User")
                .permission(sharedNote.getPermission())
                .createdAt(sharedNote.getCreatedAt())
                .updatedAt(sharedNote.getUpdatedAt())
                .build();
    }
}
