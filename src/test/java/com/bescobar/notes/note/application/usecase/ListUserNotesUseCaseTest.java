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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit test for ListUserNotesUseCase")
public class ListUserNotesUseCaseTest {

    @Mock
    private NoteRepositoryPort noteRepositoryPort;

    @InjectMocks
    private ListUserNotesUseCase listUserNotesUseCase;

    private User owner;
    private List<Note> notes;

    @BeforeEach
    void setup() {
        notes = new ArrayList<>();
        owner = new User();

        owner.setId(1L);
        owner.setEmail("test@example.com");
        owner.setPassword("encodedPassword");
        owner.setRole(Role.REGULAR);
        owner.setActive(true);

        Note note1 = new Note(1L, "New title", "New content", owner);
        Note note2 = new Note(2L, "New title", "New content", owner);
        notes.add(note1);
        notes.add(note2);
    }

    @Test
    @DisplayName("Should list all notes associated with a user")
    void shouldListAllNotesAssociatedWithUser() {
        when(noteRepositoryPort.findByUser(owner)).thenReturn(notes);

        List<NoteDTO> response = listUserNotesUseCase.findByUser(owner);

        //Then
        assertNotNull(response);
        assertEquals(2, response.size(), "Should return exactly 2 notes");

        NoteDTO first = response.get(0);
        assertEquals(1L, first.getId());
        assertEquals("New title", first.getTitle());
        assertEquals("New content", first.getContent());

        NoteDTO second = response.get(1);
        assertEquals(2L, second.getId());
        assertEquals("New title", second.getTitle());
        assertEquals("New content", second.getContent());
    }

    @Test
    @DisplayName("should return an empty list when the user has no associated notes")
    void shouldReturnAnEmptyListWhenTheUserHasNoAssociatedNotes() {
        when(noteRepositoryPort.findByUser(owner)).thenReturn(Collections.emptyList());
        
        List<NoteDTO> response = listUserNotesUseCase.findByUser(owner);

        assertNotNull(response);
        assertEquals(0, response.size(), "Should return an empty list");
    }

}
