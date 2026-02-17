package com.bescobar.notes.noteshare.application.usecase;

import com.bescobar.notes.noteshare.application.port.in.GetMySharedNotesInputPort;
import com.bescobar.notes.noteshare.application.port.in.query.SharedNoteDTO;
import com.bescobar.notes.noteshare.application.port.out.NoteShareRepositoryPort;
import com.bescobar.notes.noteshare.domain.model.SharedNote;
import com.bescobar.notes.note.application.port.out.NoteRepositoryPort;
import com.bescobar.notes.note.domain.model.Note;
import com.bescobar.notes.user.application.port.out.UserRepositoryPort;
import com.bescobar.notes.user.domain.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case for getting notes shared by the current user.
 */
@Service
@AllArgsConstructor
public class GetMySharedNotesUseCase implements GetMySharedNotesInputPort {

    private final NoteShareRepositoryPort noteShareRepository;
    private final NoteRepositoryPort noteRepository;
    private final UserRepositoryPort userRepository;

    @Override
    public List<SharedNoteDTO> getNotesSharedByMe(Long currentUserId) {
        List<SharedNote> sharedNotes = noteShareRepository.findBySharedByUserId(currentUserId);

        return sharedNotes.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
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
