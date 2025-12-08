package com.bescobar.notes.note.application.usecase;

import com.bescobar.notes.note.application.port.in.command.UpdateNoteCommand;
import com.bescobar.notes.note.application.port.in.query.NoteDTO;
import com.bescobar.notes.note.application.port.out.NoteRepositoryPort;
import com.bescobar.notes.note.domain.model.Note;
import com.bescobar.notes.user.domain.model.Role;
import com.bescobar.notes.user.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit test for UpdateNoteUseCase")
public class UpdateNoteUseCaseTest {

    @Mock
    private NoteRepositoryPort noteRepositoryPort;

    @InjectMocks
    private UpdateNoteUseCase updateNoteUseCase;

    private UpdateNoteCommand updateNoteCommand;
    private Note note;
    private User owner;

    @BeforeEach
    void setup() {
        owner = new User();
        owner.setId(1L);
        owner.setEmail("test@example.com");
        owner.setUsername("testuser");
        owner.setPassword("encodedPassword");
        owner.setRole(Role.REGULAR);
        owner.setActive(true);

        note = new Note(1L, "New title", "New content", owner);


        updateNoteCommand = UpdateNoteCommand.builder().build();
        updateNoteCommand.setTitle("Update title");
        updateNoteCommand.setContent("Update content");
        updateNoteCommand.setOwner(owner);
    }

    @Test
    @DisplayName("Should update a note successfully when parameters are valid")
    void shouldUpdateANoteSuccessfullyWhenParametersAreValid() {

        when(noteRepositoryPort.findById(anyLong())).thenReturn(Optional.of(note));

        when(noteRepositoryPort.update(org.mockito.ArgumentMatchers.any(Note.class), anyLong())).thenAnswer(invocation -> {
            Note update = invocation.getArgument(0);
            return update;
        });
        NoteDTO result = updateNoteUseCase.updateNote(updateNoteCommand, 1L);

        assertNotNull(result);
        assertEquals("Update title", result.getTitle());
        assertEquals("Update content", result.getContent());
        assertEquals(note.getOwner().getId(), result.getUserId());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when the title is empty")
    void shouldThrowExceptionWhenTitleIsEmpty() {
        UpdateNoteCommand invalidCommand = UpdateNoteCommand.builder()
                .title("")
                .content("updated content")
                .owner(owner)
                .build();

        assertThrows(IllegalArgumentException.class, ()
                -> updateNoteUseCase.updateNote(invalidCommand, 1L));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when the note does not exist")
    void shouldThrowExceptionWhenNoteDoesNotExist() {

        when(noteRepositoryPort.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> updateNoteUseCase.updateNote(updateNoteCommand, 1L));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when the user is not the owner of the note")
    void shouldThrowExceptionWhenUserIsNotOwner() {

        User anotherUser = new User();
        anotherUser.setId(99L);
        updateNoteCommand.setOwner(anotherUser);

        when(noteRepositoryPort.findById(anyLong())).thenReturn(Optional.of(note));

        assertThrows(IllegalArgumentException.class,
                () -> updateNoteUseCase.updateNote(updateNoteCommand, 1L));
    }
}
