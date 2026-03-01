package com.bescobar.notes.noteshare.application.usecase;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bescobar.notes.friendship.application.port.out.FriendshipRepositoryOutputPort;
import com.bescobar.notes.friendship.domain.model.FriendshipStatus;
import com.bescobar.notes.noteshare.application.port.in.ShareNoteInputPort;
import com.bescobar.notes.noteshare.application.port.in.command.ShareNoteCommand;
import com.bescobar.notes.noteshare.application.port.out.NoteShareRepositoryOutputPort;
import com.bescobar.notes.noteshare.domain.exception.NoteNotFoundException;
import com.bescobar.notes.noteshare.domain.exception.NoteShareBusinessRuleException;
import com.bescobar.notes.noteshare.domain.model.SharedNote;
import com.bescobar.notes.note.application.port.out.NoteRepositoryOutputPort;
import com.bescobar.notes.note.domain.model.Note;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;

/**
 * Use case for sharing a note with another user.
 *
 * Business rules:
 * 1. The note must exist and belong to the current user
 * 2. The users must be friends (friendship status ACCEPTED)
 * 3. Cannot share with yourself
 * 4. Cannot share the same note with the same user twice
 */
@Service
@AllArgsConstructor
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Spring-managed dependency injection")
public class ShareNoteUseCase implements ShareNoteInputPort {

    private final NoteShareRepositoryOutputPort noteShareRepository;
    private final NoteRepositoryOutputPort noteRepository;
    private final FriendshipRepositoryOutputPort friendshipRepository;

    @Override
    @Transactional
    public SharedNote shareNote(Long currentUserId, ShareNoteCommand command) {
        Note note = noteRepository.findById(command.getNoteId())
                .orElseThrow(() -> new NoteNotFoundException(command.getNoteId()));

        if (!note.getOwner().getId().equals(currentUserId)) {
            throw new NoteShareBusinessRuleException("You can only share your own notes");
        }

        if (currentUserId.equals(command.getSharedWithUserId())) {
            throw new NoteShareBusinessRuleException("You cannot share a note with yourself");
        }

        boolean areFriends = friendshipRepository.existsByStatusInBetweenUsers(
                Arrays.asList(FriendshipStatus.ACCEPTED),
                currentUserId,
                command.getSharedWithUserId()
        );

        if (!areFriends) {
            throw new NoteShareBusinessRuleException("You can only share notes with friends");
        }

        if (noteShareRepository.existsByNoteIdAndSharedWithUserId(command.getNoteId(), command.getSharedWithUserId())) {
            throw new NoteShareBusinessRuleException("Note already shared with this user");
        }

        SharedNote sharedNote = SharedNote.builder()
                .noteId(command.getNoteId())
                .sharedByUserId(currentUserId)
                .sharedWithUserId(command.getSharedWithUserId())
                .permission(command.getPermission())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return noteShareRepository.save(sharedNote);
    }
}
