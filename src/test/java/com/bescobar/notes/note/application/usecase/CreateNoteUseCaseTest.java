package com.bescobar.notes.note.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bescobar.notes.note.application.port.in.command.CreateNoteCommand;
import com.bescobar.notes.note.application.port.in.query.NoteDTO;
import com.bescobar.notes.note.application.port.out.NoteRepositoryPort;
import com.bescobar.notes.note.domain.model.Note;
import com.bescobar.notes.user.application.port.out.UserRepositoryPort;
import com.bescobar.notes.user.domain.model.Role;
import com.bescobar.notes.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("Create note service Unit Test")
public class CreateNoteUseCaseTest {

    @Mock
    private NoteRepositoryPort noteRepositoryPort;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private CreateNoteUseCase createNoteService;

    private User testUser;

    private CreateNoteCommand createNoteCommand;

    @BeforeEach
    void setup() {
        // create user
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.REGULAR);
        testUser.setActive(true);

        // create note command
        createNoteCommand = CreateNoteCommand.builder()
                .title("New title")
                .content("new content")
                .userId(testUser.getId())
                .build();

    }

    @Test
    @DisplayName("Should successfully create note")
    void shouldCreateNoteWithValidParams() {
        //Given
        when(userRepositoryPort.findById(testUser.getId())).thenReturn(testUser);

        Note noteSave = new Note(1L, "New title", "New content", testUser);
        when(noteRepositoryPort.save(any(Note.class))).thenReturn(noteSave);
        //When
        NoteDTO response = createNoteService.create(createNoteCommand);

        //Then
        assertNotNull(response);
        assertEquals("New title", response.getTitle());
        assertEquals("New content", response.getContent());
        assertEquals(1L, response.getId());

        ArgumentCaptor<Note> noteCaptor = ArgumentCaptor.forClass(Note.class);
        verify(noteRepositoryPort).save(noteCaptor.capture());
        verify(userRepositoryPort).findById(testUser.getId());
        verifyNoMoreInteractions(noteRepositoryPort);

        Note captured = noteCaptor.getValue();
        assertEquals("New title", captured.getTitle());
        assertEquals("new content", captured.getContent());
        assertEquals(testUser, captured.getOwner());

    }
    @Test
    @DisplayName("It should return an error when trying to create a note with invalid parameters.")
    void shouldReturnAnErrorWhenTryingToCreateANoteWithInvalidParams (){
        CreateNoteCommand createNoteCommand = CreateNoteCommand.builder()
                .title("")
                .content("new content")
                .userId(testUser.getId())
                .build();
        assertThrows(IllegalArgumentException.class, ()
                -> createNoteService.create(createNoteCommand));

        verify(userRepositoryPort, never()).findById(anyLong());
        verify(noteRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when user does not exist")
    void shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepositoryPort.findById(1L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, ()
                -> createNoteService.create(createNoteCommand));

        verify(noteRepositoryPort, never()).save(any());
    }

}
