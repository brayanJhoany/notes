package com.bescobar.notes.user.infrastructure.config;

import org.springframework.stereotype.Component;

import com.bescobar.notes.shared.security.SecurityEndpointsProvider;

@Component
public class UserSecurityConfig implements SecurityEndpointsProvider {

    @Override
    public String[] getPublicEndpoints() {
        return new String[]{
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/refresh-token"
        };
    }
}
