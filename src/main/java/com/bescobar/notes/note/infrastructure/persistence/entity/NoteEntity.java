package com.bescobar.notes.note.infrastructure.persistence.entity;

import com.bescobar.notes.user.infrastructure.persistence.entity.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notes")
public class NoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    @Size(min = 3, max = 500, message = "The title must be between 3 and 500 characters long.")
    @NotBlank(message = "The title is required.")
    private String title;

    @Column(nullable = false, length = 5000)
    @Size(min = 3, max = 5000, message = "The content must be between 3 and 5000 characters long.")
    @NotBlank(message = "The content is required.")
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity owner;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
