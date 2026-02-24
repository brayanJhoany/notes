package com.bescobar.notes.user.infrastructure.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.bescobar.notes.user.infrastructure.persistence.repository.UserJpaRepository;
import com.bescobar.notes.user.infrastructure.persistence.repository.RefreshTokenJpaRepository;
import com.bescobar.notes.user.infrastructure.web.dto.UserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@DisplayName("UserController Integration Tests")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private RefreshTokenJpaRepository refreshTokenRepository;

    private String accessToken;

    @BeforeEach
    void setUp() throws Exception {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();

        accessToken = registerAndGetAccessToken("test@example.com", "Test User");
        registerAndGetAccessToken("other@example.com", "Other User");
    }

    @Test
    @DisplayName("GET /api/users - Should fail without authentication")
    void shouldFailWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/users - Should list users for authenticated user")
    void shouldListUsersWithAuthentication() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.numberOfElements").value(2))
                .andExpect(jsonPath("$.content[0].email").exists())
                .andExpect(jsonPath("$.content[1].email").exists())
                .andExpect(jsonPath("$.content[0].password").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/users?email=... - Should filter users by email")
    void shouldFilterUsersByEmail() throws Exception {
        mockMvc.perform(get("/api/users")
                        .queryParam("email", "other@")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfElements").value(1))
                .andExpect(jsonPath("$.content[0].email").value("other@example.com"));
    }

    @Test
    @DisplayName("GET /api/users?fullname=... - Should filter users by full name")
    void shouldFilterUsersByFullName() throws Exception {
        mockMvc.perform(get("/api/users")
                        .queryParam("fullname", "Other")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfElements").value(1))
                .andExpect(jsonPath("$.content[0].fullName").value("Other User"));
    }

    private String registerAndGetAccessToken(String email, String fullName) throws Exception {
        UserRequest request = new UserRequest();
        request.setFullName(fullName);
        request.setEmail(email);
        request.setPassword("password123");
        request.setPhone("+1234567890");
        request.setAddress("123 Test St");

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("accessToken").asText();
    }
}
