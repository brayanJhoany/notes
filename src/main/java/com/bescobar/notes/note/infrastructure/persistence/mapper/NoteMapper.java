package com.bescobar.notes.note.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import com.bescobar.notes.note.domain.model.Note;
import com.bescobar.notes.note.infrastructure.persistence.entity.NoteEntity;
import com.bescobar.notes.user.infrastructure.persistence.mapper.UserMapper;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class NoteMapper {

    private final UserMapper userMapper;

    public NoteEntity toEntity(@NotNull Note noteModel) {
        NoteEntity entity = new NoteEntity();
        entity.setId(noteModel.getId());
        entity.setTitle(noteModel.getTitle());
        entity.setContent(noteModel.getContent());
        entity.setCreatedAt(noteModel.getCreatedAt());
        entity.setOwner(userMapper.toEntity(noteModel.getOwner()));
        return entity;
    }

    public Note toDomain(@NotNull NoteEntity entity) {
        return new Note(
            entity.getId(),
            entity.getTitle(),
            entity.getContent(),
            userMapper.toDomain(entity.getOwner())
        );
    }


}
