package com.bescobar.notes.note.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bescobar.notes.note.infrastructure.persistence.entity.NoteEntity;
import com.bescobar.notes.user.infrastructure.persistence.entity.UserEntity;

@Repository
public interface NoteJpaRepository extends JpaRepository<NoteEntity, Long> {
    List<NoteEntity> findByOwner(UserEntity owner);
}
