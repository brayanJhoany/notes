package com.bescobar.notes.note.application.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bescobar.notes.note.application.port.in.FindNoteUseCase;
import com.bescobar.notes.note.application.port.out.NoteRepositoryPort;

@ExtendWith(MockitoExtension.class)
@DisplayName("Find note use case Unit Test")
public class FindNoteUseCaseTest {
    
    @Mock
    private NoteRepositoryPort noteRepositoryPort;

    @InjectMocks
    private FindNoteUseCase findNoteUseCase;

    @BeforeEach
    void setup(){

    }

    @Test
    @DisplayName("should find note when it exists")
    void shouldFindNoteWhenItExistst(){
        
    }
}
