package com.bescobar.notes.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom Access Denied Handler
 *
 * Handles authorization errors (403 Forbidden) and provides
 * detailed JSON responses when authenticated users lack permissions.
 * This handler is triggered at the Spring Security filter level,
 * before requests reach the controllers.
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        errorDetails.put("status", HttpServletResponse.SC_FORBIDDEN);
        errorDetails.put("error", "Forbidden");
        errorDetails.put("message", "No tiene permisos suficientes para acceder a este recurso.");

        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }
}
