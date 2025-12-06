package com.bescobar.notes.note.domain.model;

import com.bescobar.notes.user.domain.model.User;

import java.time.LocalDateTime;

public class Note {

    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;
    private final User owner;


    public Note(Long id, String title, String content, User owner) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.owner = owner;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User getOwner() {
        return owner;
    }
}
