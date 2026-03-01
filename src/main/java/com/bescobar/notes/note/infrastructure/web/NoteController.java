package com.bescobar.notes.note.infrastructure.web;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bescobar.notes.note.application.port.in.CreateNoteInputPort;
import com.bescobar.notes.note.application.port.in.DeleteNoteInputPort;
import com.bescobar.notes.note.application.port.in.FindNoteInputPort;
import com.bescobar.notes.note.application.port.in.ListUserNotesInputPort;
import com.bescobar.notes.note.application.port.in.UpdateNoteInputPort;
import com.bescobar.notes.note.application.port.in.command.CreateNoteCommand;
import com.bescobar.notes.note.application.port.in.command.UpdateNoteCommand;
import com.bescobar.notes.note.application.port.in.query.NoteDTO;
import com.bescobar.notes.note.infrastructure.web.dto.NoteWebRequest;
import com.bescobar.notes.note.infrastructure.web.dto.NoteWebResponse;
import com.bescobar.notes.note.infrastructure.web.mapper.NoteWebMapper;
import com.bescobar.notes.shared.security.AuthenticatedUser;
import com.bescobar.notes.user.domain.model.User;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/notes")
@AllArgsConstructor
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Spring-managed dependency injection")
public class NoteController {

    private final CreateNoteInputPort createNoteUseCase;
    private final ListUserNotesInputPort listUserNotesUseCase;
    private final FindNoteInputPort findNoteUseCase;
    private final UpdateNoteInputPort updateNoteUseCase;
    private final NoteWebMapper noteWebMapper;
    private final DeleteNoteInputPort deleteNoteUseCase;

    @PostMapping
    public ResponseEntity<NoteWebResponse> store(
            @AuthenticatedUser User currentUser,
            @Valid @RequestBody NoteWebRequest webRequest) {
        CreateNoteCommand command = CreateNoteCommand.builder()
                .title(webRequest.getTitle())
                .content(webRequest.getContent())
                .userId(currentUser.getId())
                .build();
        NoteDTO noteDTO = createNoteUseCase.create(command);
        NoteWebResponse webResponse = noteWebMapper.toWebResponse(noteDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(webResponse);
    }

    @GetMapping
    public ResponseEntity<List<NoteWebResponse>> getNotes(@AuthenticatedUser User currentUser) {
        List<NoteDTO> noteDTOs = listUserNotesUseCase.findByUser(currentUser);
        List<NoteWebResponse> webResponses = noteWebMapper.toWebResponseList(noteDTOs);

        return ResponseEntity.ok(webResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteWebResponse> getNote(
            @AuthenticatedUser User currentUser,
            @PathVariable Long id) {
        NoteDTO noteDTO = findNoteUseCase.findById(currentUser, id);
        NoteWebResponse webResponse = noteWebMapper.toWebResponse(noteDTO);

        return ResponseEntity.ok(webResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteWebResponse> updateNote(
            @AuthenticatedUser User currentUser,
            @PathVariable Long id,
            @Valid @RequestBody NoteWebRequest webRequest) {
        UpdateNoteCommand command = UpdateNoteCommand.builder()
                .title(webRequest.getTitle())
                .content(webRequest.getContent())
                .owner(currentUser)
                .build();

        NoteDTO noteDTO = updateNoteUseCase.updateNote(command, id);
        NoteWebResponse webResponse = noteWebMapper.toWebResponse(noteDTO);

        return ResponseEntity.ok(webResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(
        @AuthenticatedUser User currentUser,
        @PathVariable Long id
    ){
        deleteNoteUseCase.deleteNoteById(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
