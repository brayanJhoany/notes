package com.bescobar.notes.noteshare.infrastructure.config;

import com.bescobar.notes.shared.security.SecurityEndpointsProvider;
import org.springframework.stereotype.Component;

/**
 * Security configuration for NoteShare module.
 * All note sharing endpoints require authentication.
 */
@Component
public class NoteShareSecurityConfig implements SecurityEndpointsProvider {

    @Override
    public String[] getPublicEndpoints() {
        // No public endpoints - all note sharing operations require authentication
        return new String[]{};
    }
}
