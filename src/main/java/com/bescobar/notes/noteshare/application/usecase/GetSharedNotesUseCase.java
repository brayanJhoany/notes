package com.bescobar.notes.noteshare.application.usecase;

import com.bescobar.notes.noteshare.application.port.in.GetSharedNotesInputPort;
import com.bescobar.notes.noteshare.application.port.in.query.SharedNoteDTO;
import com.bescobar.notes.noteshare.application.port.out.NoteShareRepositoryOutputPort;
import com.bescobar.notes.noteshare.domain.model.SharedNote;
import com.bescobar.notes.note.application.port.out.NoteRepositoryOutputPort;
import com.bescobar.notes.note.domain.model.Note;
import com.bescobar.notes.user.application.port.out.UserRepositoryOutputPort;
import com.bescobar.notes.user.domain.model.User;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case for getting notes shared with the current user.
 */
@Service
@AllArgsConstructor
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Spring-managed dependency injection")
public class GetSharedNotesUseCase implements GetSharedNotesInputPort {

    private final NoteShareRepositoryOutputPort noteShareRepository;
    private final NoteRepositoryOutputPort noteRepository;
    private final UserRepositoryOutputPort userRepository;

    @Override
    public List<SharedNoteDTO> getNotesSharedWithMe(Long currentUserId) {
        List<SharedNote> sharedNotes = noteShareRepository.findBySharedWithUserId(currentUserId);

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
