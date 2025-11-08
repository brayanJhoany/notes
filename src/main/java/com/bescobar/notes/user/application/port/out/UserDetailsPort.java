package com.bescobar.notes.user.application.port.out;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Port for loading user authentication details.
 * This abstraction allows the application layer to load user details
 * without depending directly on Spring Security's UserDetailsService.
 */
public interface UserDetailsPort {

    /**
     * Loads user details by email address.
     *
     * @param email the email address of the user
     * @return UserDetails containing user authentication information
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException if user not found
     */
    UserDetails loadUserByEmail(String email);
}
