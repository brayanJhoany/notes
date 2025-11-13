package com.bescobar.notes.user.infrastructure.web;

import com.bescobar.notes.user.infrastructure.persistence.entity.UserEntity;
import com.bescobar.notes.user.infrastructure.persistence.repository.UserJpaRepository;
import com.bescobar.notes.user.infrastructure.web.dto.UpdateUserRequest;
import com.bescobar.notes.user.infrastructure.web.dto.UserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@DisplayName("ProfileController Integration Tests")
class ProfileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String accessToken;
    private UserEntity testUser;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();

        // Register a user and get access token
        UserRequest registerRequest = new UserRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setFullName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setPhone("+1234567890");
        registerRequest.setAddress("123 Test St");

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        accessToken = objectMapper.readTree(response).get("accessToken").asText();

        testUser = userRepository.findByEmail("test@example.com");
    }

    @Test
    @DisplayName("GET /api/profile - Should get current user profile")
    void shouldGetCurrentUserProfile() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/profile")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.fullName").value("Test User"))
                .andExpect(jsonPath("$.phone").value("+1234567890"))
                .andExpect(jsonPath("$.address").value("123 Test St"))
                .andExpect(jsonPath("$.role").value("REGULAR"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/profile - Should fail without authentication")
    void shouldFailWithoutAuthentication() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/profile"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/profile - Should fail with invalid token")
    void shouldFailWithInvalidToken() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/profile")
                .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /api/profile - Should update current user profile")
    void shouldUpdateProfile() throws Exception {
        // Given
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setUsername("updateduser");
        updateRequest.setFullName("Updated User");
        updateRequest.setEmail("updated@example.com");
        updateRequest.setPhone("+9876543210");
        updateRequest.setAddress("456 Updated St");

        // When & Then
        mockMvc.perform(put("/api/profile")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.username").value("updateduser"))
                .andExpect(jsonPath("$.fullName").value("Updated User"))
                .andExpect(jsonPath("$.phone").value("+9876543210"))
                .andExpect(jsonPath("$.address").value("456 Updated St"))
                .andExpect(jsonPath("$.password").doesNotExist());

        // Verify in database
        UserEntity updatedUser = userRepository.findByEmail("updated@example.com");
        assert updatedUser != null;
        assert updatedUser.getUsername().equals("updateduser");
    }

    @Test
    @DisplayName("PUT /api/profile - Should fail with duplicate email")
    void shouldFailWithDuplicateEmail() throws Exception {
        // Given - Create another user
        UserEntity anotherUser = new UserEntity();
        anotherUser.setUsername("anotheruser");
        anotherUser.setFullName("Another User");
        anotherUser.setEmail("another@example.com");
        anotherUser.setPassword(passwordEncoder.encode("password123"));
        anotherUser.setPhone("+9999999999");
        anotherUser.setAddress("999 Another St");
        anotherUser.setActive(true);
        userRepository.save(anotherUser);

        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setUsername("testuser");
        updateRequest.setFullName("Test User");
        updateRequest.setEmail("another@example.com"); // Duplicate email
        updateRequest.setPhone("+1234567890");
        updateRequest.setAddress("123 Test St");

        // When & Then
        mockMvc.perform(put("/api/profile")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/profile - Should fail with duplicate username")
    void shouldFailWithDuplicateUsername() throws Exception {
        // Given - Create another user
        UserEntity anotherUser = new UserEntity();
        anotherUser.setUsername("anotheruser");
        anotherUser.setFullName("Another User");
        anotherUser.setEmail("another@example.com");
        anotherUser.setPassword(passwordEncoder.encode("password123"));
        anotherUser.setPhone("+9999999999");
        anotherUser.setAddress("999 Another St");
        anotherUser.setActive(true);
        userRepository.save(anotherUser);

        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setUsername("anotheruser"); // Duplicate username
        updateRequest.setFullName("Test User");
        updateRequest.setEmail("test@example.com");
        updateRequest.setPhone("+1234567890");
        updateRequest.setAddress("123 Test St");

        // When & Then
        mockMvc.perform(put("/api/profile")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/profile - Should allow keeping the same email and username")
    void shouldAllowKeepingSameEmailAndUsername() throws Exception {
        // Given - Update with same email and username
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setUsername("testuser"); // Same username
        updateRequest.setFullName("Updated Name");
        updateRequest.setEmail("test@example.com"); // Same email
        updateRequest.setPhone("+9999999999");
        updateRequest.setAddress("New Address");

        // When & Then
        mockMvc.perform(put("/api/profile")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Updated Name"))
                .andExpect(jsonPath("$.phone").value("+9999999999"))
                .andExpect(jsonPath("$.address").value("New Address"));
    }

    @Test
    @DisplayName("PUT /api/profile - Should fail without authentication")
    void shouldFailUpdateWithoutAuthentication() throws Exception {
        // Given
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setUsername("updateduser");
        updateRequest.setFullName("Updated User");
        updateRequest.setEmail("updated@example.com");

        // When & Then
        mockMvc.perform(put("/api/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /api/profile - Should fail with invalid data")
    void shouldFailWithInvalidData() throws Exception {
        // Given - Invalid email format
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setUsername("updateduser");
        updateRequest.setFullName("Updated User");
        updateRequest.setEmail("invalid-email"); // Invalid email
        updateRequest.setPhone("+1234567890");
        updateRequest.setAddress("123 Test St");

        // When & Then
        mockMvc.perform(put("/api/profile")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }
}
