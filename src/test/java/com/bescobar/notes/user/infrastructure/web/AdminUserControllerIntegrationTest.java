package com.bescobar.notes.user.infrastructure.web;

import com.bescobar.notes.user.infrastructure.persistence.entity.RoleEntity;
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
@DisplayName("AdminUserController Integration Tests")
class AdminUserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminAccessToken;
    private String regularUserAccessToken;
    private UserEntity regularUser;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();

        // Create and authenticate admin user
        UserRequest adminRegisterRequest = new UserRequest();
        adminRegisterRequest.setUsername("adminuser");
        adminRegisterRequest.setFullName("Admin User");
        adminRegisterRequest.setEmail("admin@example.com");
        adminRegisterRequest.setPassword("password123");
        adminRegisterRequest.setPhone("+1111111111");
        adminRegisterRequest.setAddress("111 Admin St");
        adminRegisterRequest.setRole(RoleEntity.ADMIN);

        MvcResult adminResult = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminRegisterRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String adminResponse = adminResult.getResponse().getContentAsString();
        adminAccessToken = objectMapper.readTree(adminResponse).get("accessToken").asText();

        // Manually set admin role (since register always creates REGULAR users)
        UserEntity adminUser = userRepository.findByEmail("admin@example.com");
        adminUser.setRole(RoleEntity.ADMIN);
        userRepository.saveAndFlush(adminUser);

        // Create regular user
        UserRequest regularRegisterRequest = new UserRequest();
        regularRegisterRequest.setUsername("regularuser");
        regularRegisterRequest.setFullName("Regular User");
        regularRegisterRequest.setEmail("regular@example.com");
        regularRegisterRequest.setPassword("password123");
        regularRegisterRequest.setPhone("+2222222222");
        regularRegisterRequest.setAddress("222 Regular St");

        MvcResult regularResult = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regularRegisterRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String regularResponse = regularResult.getResponse().getContentAsString();
        regularUserAccessToken = objectMapper.readTree(regularResponse).get("accessToken").asText();

        regularUser = userRepository.findByEmail("regular@example.com");
    }

    @Test
    @DisplayName("GET /api/admin/users - Admin should get all users")
    void adminShouldGetAllUsers() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/users")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].email").exists())
                .andExpect(jsonPath("$[1].email").exists());
    }

    @Test
    @DisplayName("GET /api/admin/users - Regular user should be forbidden")
    void regularUserShouldBeForbidden() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/users")
                .header("Authorization", "Bearer " + regularUserAccessToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/admin/users - Should fail without authentication")
    void shouldFailWithoutAuthentication() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/admin/users/{id} - Admin should get user by id")
    void adminShouldGetUserById() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/users/" + regularUser.getId())
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("regular@example.com"))
                .andExpect(jsonPath("$.username").value("regularuser"))
                .andExpect(jsonPath("$.fullName").value("Regular User"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/admin/users/{id} - Should fail with non-existent id")
    void shouldFailWithNonExistentId() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/admin/users/99999")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/admin/users/{id} - Admin should update user")
    void adminShouldUpdateUser() throws Exception {
        // Given
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setUsername("updateduser");
        updateRequest.setFullName("Updated User");
        updateRequest.setEmail("updated@example.com");
        updateRequest.setPhone("+9999999999");
        updateRequest.setAddress("Updated Address");

        // When & Then
        mockMvc.perform(put("/api/admin/users/" + regularUser.getId())
                .header("Authorization", "Bearer " + adminAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.username").value("updateduser"))
                .andExpect(jsonPath("$.fullName").value("Updated User"))
                .andExpect(jsonPath("$.phone").value("+9999999999"))
                .andExpect(jsonPath("$.address").value("Updated Address"));
    }

    @Test
    @DisplayName("PUT /api/admin/users/{id} - Regular user should be forbidden")
    void regularUserShouldBeForbiddenToUpdate() throws Exception {
        // Given
        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setUsername("updateduser");
        updateRequest.setFullName("Updated User");
        updateRequest.setEmail("updated@example.com");

        // When & Then
        mockMvc.perform(put("/api/admin/users/" + regularUser.getId())
                .header("Authorization", "Bearer " + regularUserAccessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /api/admin/users/{id} - Admin should delete user")
    void adminShouldDeleteUser() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/admin/users/" + regularUser.getId())
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNoContent());

        // Verify user is deleted
        mockMvc.perform(get("/api/admin/users/" + regularUser.getId())
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/admin/users/{id} - Regular user should be forbidden")
    void regularUserShouldBeForbiddenToDelete() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/admin/users/" + regularUser.getId())
                .header("Authorization", "Bearer " + regularUserAccessToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /api/admin/users/{id} - Should fail with non-existent id")
    void shouldFailDeleteWithNonExistentId() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/admin/users/99999")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound());
    }
}
