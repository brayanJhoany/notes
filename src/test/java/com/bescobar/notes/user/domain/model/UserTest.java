package com.bescobar.notes.user.domain.model;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("User Domain Model Tests")
class UserTest {

    @Test
    @DisplayName("Should create user with default values")
    void shouldCreateUserWithDefaults() {
        // When
        User user = new User();

        // Then
        assertNotNull(user);
        assertEquals(Role.REGULAR, user.getRole(), "Default role should be REGULAR");
        assertTrue(user.getActive(), "Default active status should be true");
    }

    @Test
    @DisplayName("Should create user with all fields")
    void shouldCreateUserWithAllFields() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setPhone("+1234567890");
        user.setAddress("123 Test St");
        user.setRole(Role.ADMIN);
        user.setActive(true);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        // Then
        assertAll("User fields should be set correctly",
            () -> assertEquals(1L, user.getId()),
            () -> assertEquals("testuser", user.getUsername()),
            () -> assertEquals("Test User", user.getFullName()),
            () -> assertEquals("test@example.com", user.getEmail()),
            () -> assertEquals("encodedPassword", user.getPassword()),
            () -> assertEquals("+1234567890", user.getPhone()),
            () -> assertEquals("123 Test St", user.getAddress()),
            () -> assertEquals(Role.ADMIN, user.getRole()),
            () -> assertTrue(user.getActive()),
            () -> assertEquals(now, user.getCreatedAt()),
            () -> assertEquals(now, user.getUpdatedAt())
        );
    }

    @Test
    @DisplayName("Should allow updating user fields")
    void shouldAllowUpdatingFields() {
        // Given
        User user = new User();
        user.setEmail("old@example.com");

        // When
        user.setEmail("new@example.com");

        // Then
        assertEquals("new@example.com", user.getEmail());
    }

    @Test
    @DisplayName("Should allow setting active status to false")
    void shouldAllowDeactivation() {
        // Given
        User user = new User();
        assertTrue(user.getActive());

        // When
        user.setActive(false);

        // Then
        assertFalse(user.getActive());
    }

    @Test
    @DisplayName("Should handle null values appropriately")
    void shouldHandleNullValues() {
        // Given
        User user = new User();

        // When
        user.setPhone(null);
        user.setAddress(null);

        // Then
        assertNull(user.getPhone());
        assertNull(user.getAddress());
    }
}
