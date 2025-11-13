package com.bescobar.notes.user.infrastructure.persistence.repository;

import com.bescobar.notes.user.infrastructure.persistence.entity.RoleEntity;
import com.bescobar.notes.user.infrastructure.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserJpaRepository Integration Tests")
class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository userRepository;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        testUser = new UserEntity();
        testUser.setUsername("testuser");
        testUser.setFullName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setPhone("+1234567890");
        testUser.setAddress("123 Test St");
        testUser.setRole(RoleEntity.REGULAR);
        testUser.setActive(true);
    }

    @Test
    @DisplayName("Should save and retrieve user")
    void shouldSaveAndRetrieveUser() {
        // When
        UserEntity savedUser = userRepository.save(testUser);

        // Then
        assertNotNull(savedUser.getId());
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("testuser", savedUser.getUsername());
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        // Given
        userRepository.save(testUser);

        // When
        UserEntity foundUser = userRepository.findByEmail("test@example.com");

        // Then
        assertNotNull(foundUser);
        assertEquals("test@example.com", foundUser.getEmail());
        assertEquals("testuser", foundUser.getUsername());
    }

    @Test
    @DisplayName("Should return null when email not found")
    void shouldReturnNullWhenEmailNotFound() {
        // When
        UserEntity foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertNull(foundUser);
    }

    @Test
    @DisplayName("Should check if email exists")
    void shouldCheckIfEmailExists() {
        // Given
        userRepository.save(testUser);

        // When & Then
        assertTrue(userRepository.existsByEmail("test@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    @DisplayName("Should check if username exists")
    void shouldCheckIfUsernameExists() {
        // Given
        userRepository.save(testUser);

        // When & Then
        assertTrue(userRepository.existsByUsername("testuser"));
        assertFalse(userRepository.existsByUsername("nonexistentuser"));
    }

    @Test
    @DisplayName("Should set timestamps on create")
    void shouldSetTimestampsOnCreate() {
        // When
        UserEntity savedUser = userRepository.save(testUser);

        // Then
        assertNotNull(savedUser.getCreatedAt());
        assertNotNull(savedUser.getUpdatedAt());
    }

    @Test
    @DisplayName("Should update user")
    void shouldUpdateUser() {
        // Given
        UserEntity savedUser = userRepository.save(testUser);
        Long userId = savedUser.getId();

        // When
        savedUser.setFullName("Updated Name");
        savedUser.setEmail("updated@example.com");
        UserEntity updatedUser = userRepository.save(savedUser);

        // Then
        assertEquals(userId, updatedUser.getId());
        assertEquals("Updated Name", updatedUser.getFullName());
        assertEquals("updated@example.com", updatedUser.getEmail());
    }

    @Test
    @DisplayName("Should delete user")
    void shouldDeleteUser() {
        // Given
        UserEntity savedUser = userRepository.save(testUser);
        Long userId = savedUser.getId();

        // When
        userRepository.deleteById(userId);

        // Then
        assertFalse(userRepository.existsById(userId));
    }

    @Test
    @DisplayName("Should save multiple users")
    void shouldSaveMultipleUsers() {
        // Given
        UserEntity user2 = new UserEntity();
        user2.setUsername("user2");
        user2.setFullName("User 2");
        user2.setEmail("user2@example.com");
        user2.setPassword("password2");
        user2.setPhone("+9876543210");
        user2.setAddress("456 Another St");
        user2.setRole(RoleEntity.REGULAR);
        user2.setActive(true);

        // When
        userRepository.save(testUser);
        userRepository.save(user2);

        // Then
        assertEquals(2, userRepository.count());
    }

    @Test
    @DisplayName("Should enforce unique email constraint")
    void shouldEnforceUniqueEmailConstraint() {
        // Given
        userRepository.save(testUser);

        UserEntity duplicateEmailUser = new UserEntity();
        duplicateEmailUser.setUsername("differentuser");
        duplicateEmailUser.setFullName("Different User");
        duplicateEmailUser.setEmail("test@example.com"); // Duplicate email
        duplicateEmailUser.setPassword("password");
        duplicateEmailUser.setPhone("+1111111111");
        duplicateEmailUser.setAddress("111 Different St");
        duplicateEmailUser.setRole(RoleEntity.REGULAR);
        duplicateEmailUser.setActive(true);

        // When & Then
        assertThrows(Exception.class, () -> {
            userRepository.save(duplicateEmailUser);
            userRepository.flush();
        });
    }

    @Test
    @DisplayName("Should enforce unique username constraint")
    void shouldEnforceUniqueUsernameConstraint() {
        // Given
        userRepository.save(testUser);

        UserEntity duplicateUsernameUser = new UserEntity();
        duplicateUsernameUser.setUsername("testuser"); // Duplicate username
        duplicateUsernameUser.setFullName("Different User");
        duplicateUsernameUser.setEmail("different@example.com");
        duplicateUsernameUser.setPassword("password");
        duplicateUsernameUser.setPhone("+2222222222");
        duplicateUsernameUser.setAddress("222 Different St");
        duplicateUsernameUser.setRole(RoleEntity.REGULAR);
        duplicateUsernameUser.setActive(true);

        // When & Then
        assertThrows(Exception.class, () -> {
            userRepository.save(duplicateUsernameUser);
            userRepository.flush();
        });
    }
}
