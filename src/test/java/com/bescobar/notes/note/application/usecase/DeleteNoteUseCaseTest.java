package com.bescobar.notes.note.application.usecase;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bescobar.notes.note.application.port.out.NoteRepositoryPort;
import com.bescobar.notes.note.domain.model.Note;
import com.bescobar.notes.user.domain.model.Role;
import com.bescobar.notes.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit test for DeleteNoteUseCase”")
public class DeleteNoteUseCaseTest {

    @Mock
    private NoteRepositoryPort noteRepositoryPort;

    @InjectMocks
    private DeleteNoteUseCase deleteNoteService;

    private User testUser;
    private Note note;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.REGULAR);
        testUser.setActive(true);

        note = new Note(1L, "new note", "new content", testUser);
    }

    @Test
    @DisplayName("should delete note when it exists")
    void shouldDeleteNoteWhenNoteExists() {
        Long noteId = note.getId();

        deleteNoteService.deleteNoteById(noteId);

        verify(noteRepositoryPort).deleteById(noteId);
    }

    @Test
    @DisplayName("should propagate repository error when the delete operation fails")
    void shouldPropagateExceptionWhenRepositoryFails() {
        Long noteId = note.getId();
        RuntimeException repositoryError = new RuntimeException("DB failure");

        doThrow(repositoryError).when(noteRepositoryPort).deleteById(noteId);

        RuntimeException thrown =
                assertThrows(RuntimeException.class, () -> deleteNoteService.deleteNoteById(noteId));

        assertSame(repositoryError, thrown);
    }
}
