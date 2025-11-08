package com.bescobar.notes.note.infrastructure.persistence;

import com.bescobar.notes.note.application.port.out.NoteRepositoryPort;
import com.bescobar.notes.note.domain.model.Note;
import com.bescobar.notes.note.infrastructure.persistence.entity.NoteEntity;
import com.bescobar.notes.note.infrastructure.persistence.mapper.NoteMapper;
import com.bescobar.notes.note.infrastructure.persistence.repository.NoteJpaRepository;
import com.bescobar.notes.user.domain.model.User;
import com.bescobar.notes.user.infrastructure.persistence.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
public class NoteRepositoryAdapter implements NoteRepositoryPort {

    private final NoteJpaRepository noteJpaRepository;
    private final NoteMapper noteMapper;
    private final UserMapper userMapper;

    @Override
    public Note save(Note note) {
        NoteEntity entity = noteMapper.toEntity(note);
        NoteEntity savedEntity = noteJpaRepository.save(entity);
        return noteMapper.toDomain(savedEntity);
    }

    @Override
    public List<Note> findByUser(User user) {
        return noteJpaRepository.findByOwner(userMapper.toEntity(user))
                .stream()
                .map(noteMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Note> findById(Long id) {
        return noteJpaRepository.findById(id)
                .map(noteMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        noteJpaRepository.deleteById(id);
    }

    @Override
    public Note update(Note note, Long id) {
        // En JPA, save() funciona tanto para crear como actualizar
        // Si la entidad tiene ID, la actualiza
        NoteEntity entity = noteMapper.toEntity(note);
        entity.setId(id); // Asegurar que use el ID correcto
        NoteEntity updatedEntity = noteJpaRepository.save(entity);
        return noteMapper.toDomain(updatedEntity);
    }
}
