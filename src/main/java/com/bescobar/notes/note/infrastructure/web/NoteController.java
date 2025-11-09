package com.bescobar.notes.note.infrastructure.web;

import com.bescobar.notes.note.application.port.in.CreateNoteUseCase;
import com.bescobar.notes.note.application.port.in.FindNoteUseCase;
import com.bescobar.notes.note.application.port.in.ListUserNotesUseCase;
import com.bescobar.notes.note.application.port.in.UpdateNoteUseCase;
import com.bescobar.notes.note.application.port.in.command.CreateNoteCommand;
import com.bescobar.notes.note.application.port.in.command.UpdateNoteCommand;
import com.bescobar.notes.note.application.port.in.query.NoteDTO;
import com.bescobar.notes.note.infrastructure.web.dto.NoteWebRequest;
import com.bescobar.notes.note.infrastructure.web.dto.NoteWebResponse;
import com.bescobar.notes.note.infrastructure.web.mapper.NoteWebMapper;
import com.bescobar.notes.shared.security.AuthenticatedUser;
import com.bescobar.notes.user.domain.model.User;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@AllArgsConstructor
public class NoteController {

    private final CreateNoteUseCase createNoteUseCase;
    private final ListUserNotesUseCase listUserNotesUseCase;
    private final FindNoteUseCase findNoteUseCase;
    private final UpdateNoteUseCase updateNoteUseCase;
    private final NoteWebMapper noteWebMapper;

    @PostMapping
    public ResponseEntity<NoteWebResponse> store(
            @AuthenticatedUser User currentUser,
            @Valid @RequestBody NoteWebRequest webRequest) {
        try {
            CreateNoteCommand command = CreateNoteCommand.builder()
                    .title(webRequest.getTitle())
                    .content(webRequest.getContent())
                    .userId(currentUser.getId())
                    .build();
            NoteDTO noteDTO = createNoteUseCase.create(command);
            NoteWebResponse webResponse = noteWebMapper.toWebResponse(noteDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(webResponse);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<NoteWebResponse>> getNotes(@AuthenticatedUser User currentUser) {
        try {
            List<NoteDTO> noteDTOs = listUserNotesUseCase.findByUser(currentUser);
            List<NoteWebResponse> webResponses = noteWebMapper.toWebResponseList(noteDTOs);

            return ResponseEntity.ok(webResponses);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteWebResponse> getNote(
            @AuthenticatedUser User currentUser,
            @PathVariable Long id) {
        try {
            NoteDTO noteDTO = findNoteUseCase.findById(currentUser, id);
            NoteWebResponse webResponse = noteWebMapper.toWebResponse(noteDTO);

            return ResponseEntity.ok(webResponse);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteWebResponse> updateNote(
            @AuthenticatedUser User currentUser,
            @PathVariable Long id,
            @Valid @RequestBody NoteWebRequest webRequest) {
        try {
            UpdateNoteCommand command = UpdateNoteCommand.builder()
                    .title(webRequest.getTitle())
                    .content(webRequest.getContent())
                    .owner(currentUser)
                    .build();

            NoteDTO noteDTO = updateNoteUseCase.updateNote(command, id);
            NoteWebResponse webResponse = noteWebMapper.toWebResponse(noteDTO);

            return ResponseEntity.ok(webResponse);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
