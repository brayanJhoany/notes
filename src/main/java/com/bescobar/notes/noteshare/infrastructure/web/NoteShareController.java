package com.bescobar.notes.noteshare.infrastructure.web;

import com.bescobar.notes.noteshare.application.port.in.CopySharedNoteInputPort;
import com.bescobar.notes.noteshare.application.port.in.GetMySharedNotesInputPort;
import com.bescobar.notes.noteshare.application.port.in.GetSharedNoteDetailInputPort;
import com.bescobar.notes.noteshare.application.port.in.GetSharedNotesInputPort;
import com.bescobar.notes.noteshare.application.port.in.ShareNoteInputPort;
import com.bescobar.notes.noteshare.application.port.in.UnshareNoteInputPort;
import com.bescobar.notes.noteshare.application.port.in.command.CopySharedNoteCommand;
import com.bescobar.notes.noteshare.application.port.in.command.ShareNoteCommand;
import com.bescobar.notes.noteshare.application.port.in.query.SharedNoteDTO;
import com.bescobar.notes.noteshare.domain.model.SharedNote;
import com.bescobar.notes.noteshare.infrastructure.web.dto.ShareNoteRequest;
import com.bescobar.notes.noteshare.infrastructure.web.dto.SharedNoteResponse;
import com.bescobar.notes.noteshare.infrastructure.web.mapper.NoteShareWebMapper;
import com.bescobar.notes.note.application.port.in.query.NoteDTO;
import com.bescobar.notes.note.domain.model.Note;
import com.bescobar.notes.note.infrastructure.web.dto.NoteWebResponse;
import com.bescobar.notes.note.infrastructure.web.mapper.NoteWebMapper;
import com.bescobar.notes.shared.security.AuthenticatedUser;
import com.bescobar.notes.user.domain.model.User;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for note sharing operations.
 *
 * Endpoints:
 * - POST /api/note-shares - Share a note with a friend
 * - GET /api/note-shares/received - Get notes shared with me
 * - GET /api/note-shares/sent - Get notes I have shared
 * - GET /api/note-shares/{id} - Get shared note details
 * - POST /api/note-shares/{id}/copy - Copy a shared note (copy-on-write)
 * - DELETE /api/note-shares/{id} - Unshare a note
 */
@RestController
@RequestMapping("/api/note-shares")
@AllArgsConstructor
public class NoteShareController {

    private final ShareNoteInputPort shareNoteUseCase;
    private final GetSharedNotesInputPort getSharedNotesUseCase;
    private final GetMySharedNotesInputPort getMySharedNotesUseCase;
    private final GetSharedNoteDetailInputPort getSharedNoteDetailUseCase;
    private final CopySharedNoteInputPort copySharedNoteUseCase;
    private final UnshareNoteInputPort unshareNoteUseCase;
    private final NoteShareWebMapper webMapper;
    private final NoteWebMapper noteWebMapper;

    /**
     * Share a note with a friend.
     */
    @PostMapping
    public ResponseEntity<SharedNoteResponse> shareNote(
            @AuthenticatedUser User currentUser,
            @Valid @RequestBody ShareNoteRequest request) {

        ShareNoteCommand command = webMapper.toCommand(request);
        SharedNote sharedNote = shareNoteUseCase.shareNote(currentUser.getId(), command);

        // Get full details for response
        SharedNoteDTO dto = getSharedNoteDetailUseCase.getSharedNoteDetail(
                currentUser.getId(),
                sharedNote.getId()
        );

        SharedNoteResponse response = webMapper.toResponse(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all notes shared with the current user.
     */
    @GetMapping("/received")
    public ResponseEntity<List<SharedNoteResponse>> getNotesSharedWithMe(
            @AuthenticatedUser User currentUser) {

        List<SharedNoteDTO> dtos = getSharedNotesUseCase.getNotesSharedWithMe(currentUser.getId());
        List<SharedNoteResponse> responses = webMapper.toResponseList(dtos);
        return ResponseEntity.ok(responses);
    }

    /**
     * Get all notes shared by the current user.
     */
    @GetMapping("/sent")
    public ResponseEntity<List<SharedNoteResponse>> getNotesSharedByMe(
            @AuthenticatedUser User currentUser) {

        List<SharedNoteDTO> dtos = getMySharedNotesUseCase.getNotesSharedByMe(currentUser.getId());
        List<SharedNoteResponse> responses = webMapper.toResponseList(dtos);
        return ResponseEntity.ok(responses);
    }

    /**
     * Get details of a specific shared note.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SharedNoteResponse> getSharedNoteDetail(
            @AuthenticatedUser User currentUser,
            @PathVariable Long id) {

        SharedNoteDTO dto = getSharedNoteDetailUseCase.getSharedNoteDetail(currentUser.getId(), id);
        SharedNoteResponse response = webMapper.toResponse(dto);
        return ResponseEntity.ok(response);
    }

    /**
     * Copy a shared note (copy-on-write pattern).
     * Creates an independent copy owned by the current user.
     */
    @PostMapping("/{id}/copy")
    public ResponseEntity<NoteWebResponse> copySharedNote(
            @AuthenticatedUser User currentUser,
            @PathVariable Long id) {

        CopySharedNoteCommand command = CopySharedNoteCommand.builder()
                .sharedNoteId(id)
                .build();

        Note copiedNote = copySharedNoteUseCase.copySharedNote(currentUser.getId(), command);

        // Convert Note to NoteDTO
        NoteDTO noteDTO = NoteDTO.builder()
                .id(copiedNote.getId())
                .title(copiedNote.getTitle())
                .content(copiedNote.getContent())
                .createdAt(copiedNote.getCreatedAt())
                .userId(copiedNote.getOwner().getId())
                .build();

        NoteWebResponse response = noteWebMapper.toWebResponse(noteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Unshare a note (remove the share).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> unshareNote(
            @AuthenticatedUser User currentUser,
            @PathVariable Long id) {

        unshareNoteUseCase.unshareNote(currentUser.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
