package com.bescobar.notes.user.infrastructure.config;

import com.bescobar.notes.shared.security.SecurityEndpointsProvider;
import org.springframework.stereotype.Component;

@Component
public class UserSecurityConfig implements SecurityEndpointsProvider {

    @Override
    public String[] getPublicEndpoints() {
        return new String[]{
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/refresh-token",
            "/api/auth/logout"
        };
    }
}