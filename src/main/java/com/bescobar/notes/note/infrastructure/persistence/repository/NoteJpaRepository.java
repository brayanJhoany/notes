package com.bescobar.notes.note.infrastructure.persistence.repository;

import com.bescobar.notes.note.infrastructure.persistence.entity.NoteEntity;
import com.bescobar.notes.user.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteJpaRepository extends JpaRepository<NoteEntity, Long> {
    List<NoteEntity> findByOwner(UserEntity owner);
}
