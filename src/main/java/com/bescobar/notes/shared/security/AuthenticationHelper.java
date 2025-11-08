package com.bescobar.notes.shared.security;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Helper class for authentication-related operations.
 * Provides clear, intuitive methods for extracting user information from Spring Security.
 */
public final class AuthenticationHelper {

    private AuthenticationHelper() {
        // Utility class - prevent instantiation
    }

    /**
     * Extracts the email from UserDetails.
     * In our system, the username field contains the user's email.
     *
     * @param userDetails The authenticated user details
     * @return The user's email address
     */
    public static String extractEmail(UserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("UserDetails cannot be null");
        }
        return userDetails.getUsername();
    }
}
