package com.bescobar.notes.NoteShare.domain.model;

/**
 * Permission levels for shared notes.
 *
 * - READ_ONLY: User can only view the note
 * - CAN_COPY: User can view and create their own copy for editing (copy-on-write)
 */
public enum SharePermission {
    READ_ONLY,
    CAN_COPY
}
