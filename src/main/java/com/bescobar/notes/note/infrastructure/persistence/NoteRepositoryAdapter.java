package com.bescobar.notes.note.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.bescobar.notes.note.application.port.out.NoteRepositoryOutputPort;
import com.bescobar.notes.note.domain.model.Note;
import com.bescobar.notes.note.infrastructure.persistence.entity.NoteEntity;
import com.bescobar.notes.note.infrastructure.persistence.mapper.NoteMapper;
import com.bescobar.notes.note.infrastructure.persistence.repository.NoteJpaRepository;
import com.bescobar.notes.user.domain.model.User;
import com.bescobar.notes.user.infrastructure.persistence.mapper.UserMapper;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class NoteRepositoryAdapter implements NoteRepositoryOutputPort {

    private final NoteJpaRepository noteJpaRepository;
    private final NoteMapper noteMapper;
    private final UserMapper userMapper;

    @Override
    public Note save(@NotNull Note note) {
        NoteEntity entity = noteMapper.toEntity(note);
        NoteEntity savedEntity = noteJpaRepository.save(entity);
        return noteMapper.toDomain(savedEntity);
    }

    @Override
    public List<Note> findByUser(@NotNull  User user) {
        return noteJpaRepository.findByOwner(userMapper.toEntity(user))
                .stream()
                .map(noteMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Note> findById(@NotNull Long id) {
        return noteJpaRepository.findById(id)
                .map(noteMapper::toDomain);
    }

    @Override
    public void deleteById(@NotNull Long id) {
        noteJpaRepository.deleteById(id);
    }

    @Override
    public Note update(Note note, Long id) {
        NoteEntity entity = noteMapper.toEntity(note);
        entity.setId(id);
        NoteEntity updatedEntity = noteJpaRepository.save(entity);
        return noteMapper.toDomain(updatedEntity);
    }
}
