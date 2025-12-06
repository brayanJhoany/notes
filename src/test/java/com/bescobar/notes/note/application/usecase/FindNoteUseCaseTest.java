package com.bescobar.notes.note.application.usecase;

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
@DisplayName("Unit test for FindNoteUseCase")
public class FindNoteUseCaseTest {

    @Mock
    private NoteRepositoryPort noteRepositoryPort;

    @InjectMocks
    private FindNoteUseCase findNoteUseCase;

    private User testUser;
    private Note note;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.REGULAR);
        testUser.setActive(true);

        note = new Note(1L, "new note", "new content", testUser);
    }

    @Test
    @DisplayName("should find note when it exists")
    void shouldFindNoteWhenItExistst() {
        //Given
        when(noteRepositoryPort.findById(anyLong()))
                .thenReturn(Optional.of(note));

        //When
        NoteDTO noteDto = findNoteUseCase.findById(testUser, note.getId());

        //Then
        assertNotNull(noteDto);
        assertEquals("new note", noteDto.getTitle());
        assertEquals("new content", noteDto.getContent());
        assertEquals(testUser.getId(), noteDto.getUserId());
    }

    @Test
    @DisplayName("Throws IllegalArgumentException when the repository cannot find the note.")
    void shouldReturnAnExeptionWhenNoteNotFound() {

        assertThrows(IllegalArgumentException.class, ()
                -> findNoteUseCase.findById(testUser, note.getId()));
    }

    @Test
    @DisplayName("Throws IllegalArgumentException when the user is not the owner of the note")
    void shouldReturnAnExeptionWhenTheUserIsNotTheOwnerOfTheNote() {

        User newUser = new User();
        newUser.setId(1L);
        newUser.setEmail("test2@example.com");
        newUser.setUsername("newUser");
        newUser.setPassword("encodedPassword");
        newUser.setRole(Role.REGULAR);
        newUser.setActive(true);

        assertThrows(IllegalArgumentException.class, ()
                -> findNoteUseCase.findById(newUser, note.getId()));


    }

}
