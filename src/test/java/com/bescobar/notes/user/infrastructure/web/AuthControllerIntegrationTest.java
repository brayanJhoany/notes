package com.bescobar.notes.user.infrastructure.web;

import com.bescobar.notes.user.infrastructure.persistence.entity.UserEntity;
import com.bescobar.notes.user.infrastructure.persistence.repository.RefreshTokenJpaRepository;
import com.bescobar.notes.user.infrastructure.persistence.repository.UserJpaRepository;
import com.bescobar.notes.user.infrastructure.web.dto.RefreshTokenRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@DisplayName("AuthController Integration Tests")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private RefreshTokenJpaRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/auth/register - Should successfully register a new user")
    void shouldRegisterNewUser() throws Exception {
        // Given
        UserRequest request = new UserRequest();
        request.setFullName("Test User");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setPhone("+1234567890");
        request.setAddress("123 Test St");

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.user.fullName").value("Test User"))
                .andExpect(jsonPath("$.user.role").value("REGULAR"))
                .andExpect(jsonPath("$.user.active").value(true))
                .andExpect(jsonPath("$.user.password").doesNotExist());
    }

    @Test
    @DisplayName("POST /api/auth/register - Should fail with duplicate email")
    void shouldFailWithDuplicateEmail() throws Exception {
        // Given - Create existing user
        UserEntity existingUser = new UserEntity();
        existingUser.setFullName("Existing User");
        existingUser.setEmail("existing@example.com");
        existingUser.setPassword(passwordEncoder.encode("password123"));
        existingUser.setPhone("+1111111111");
        existingUser.setAddress("111 Existing St");
        userRepository.saveAndFlush(existingUser);

        UserRequest request = new UserRequest();
        request.setFullName("New User");
        request.setEmail("existing@example.com"); // Duplicate email
        request.setPassword("password123");
        request.setPhone("+2222222222");
        request.setAddress("222 New St");

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register - Should fail with invalid email")
    void shouldFailWithInvalidEmail() throws Exception {
        // Given
        UserRequest request = new UserRequest();
        request.setFullName("Test User");
        request.setEmail("invalid-email"); // Invalid email format
        request.setPassword("password123");

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login - Should successfully login with valid credentials")
    void shouldLoginSuccessfully() throws Exception {
        // Given - Create user
        UserEntity user = new UserEntity();
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setPhone("+1234567890");
        user.setAddress("123 Test St");
        user.setActive(true);
        userRepository.saveAndFlush(user);

        String loginJson = "{\"email\":\"test@example.com\",\"password\":\"password123\"}";

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.user.email").value("test@example.com"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Should fail with wrong password")
    void shouldFailWithWrongPassword() throws Exception {
        // Given - Create user
        UserEntity user = new UserEntity();
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setPhone("+1234567890");
        user.setAddress("123 Test St");
        user.setActive(true);
        userRepository.saveAndFlush(user);

        String loginJson = "{\"email\":\"test@example.com\",\"password\":\"wrongpassword\"}";

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/login - Should fail with non-existent email")
    void shouldFailWithNonExistentEmail() throws Exception {
        // Given
        String loginJson = "{\"email\":\"nonexistent@example.com\",\"password\":\"password123\"}";

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/login - Should fail when user is inactive")
    void shouldFailWhenUserInactive() throws Exception {
        // Given - Create inactive user
        UserEntity user = new UserEntity();
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setPhone("+1234567890");
        user.setAddress("123 Test St");
        user.setActive(false); // Inactive user
        userRepository.saveAndFlush(user);

        String loginJson = "{\"email\":\"test@example.com\",\"password\":\"password123\"}";

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/refresh-token - Should successfully refresh tokens")
    void shouldRefreshTokens() throws Exception {
        // Given - Register user to get tokens
        UserRequest registerRequest = new UserRequest();
        registerRequest.setFullName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setPhone("+1234567890");
        registerRequest.setAddress("123 Test St");

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String registerResponse = registerResult.getResponse().getContentAsString();
        String refreshToken = objectMapper.readTree(registerResponse).get("refreshToken").asText();

        RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
        refreshRequest.setRefreshToken(refreshToken);

        // When & Then
        mockMvc.perform(post("/api/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.user.email").value("test@example.com"));
    }

    @Test
    @DisplayName("POST /api/auth/refresh-token - Should fail with invalid token")
    void shouldFailWithInvalidRefreshToken() throws Exception {
        // Given
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
        refreshRequest.setRefreshToken("invalid.refresh.token");

        // When & Then
        mockMvc.perform(post("/api/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/logout - Should successfully logout user")
    void shouldLogoutSuccessfully() throws Exception {
        // Given - Register user to get tokens
        UserRequest registerRequest = new UserRequest();
        registerRequest.setFullName("Test User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setPhone("+1234567890");
        registerRequest.setAddress("123 Test St");

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String registerResponse = registerResult.getResponse().getContentAsString();
        String refreshToken = objectMapper.readTree(registerResponse).get("refreshToken").asText();

        RefreshTokenRequest logoutRequest = new RefreshTokenRequest();
        logoutRequest.setRefreshToken(refreshToken);

        // When & Then
        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(status().isOk());

        // Verify token is deleted - trying to refresh should fail
        mockMvc.perform(post("/api/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(status().isUnauthorized());
    }
}
