package com.bescobar.notes.shared.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to inject the authenticated User directly into controller methods.
 * Eliminates the need to extract email from UserDetails and call UserUseCase.
 *
 * Usage:
 * <pre>
 * {@code
 * @PostMapping
 * public ResponseEntity<?> createNote(
 *     @AuthenticatedUser User currentUser,
 *     @RequestBody NoteRequest request) {
 * }
 * }
 * </pre>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthenticatedUser {
}
