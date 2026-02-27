package com.bescobar.notes.user.infrastructure.adapter.security;

import com.bescobar.notes.user.application.port.out.UserDetailsOutputPort;
import com.bescobar.notes.user.infrastructure.persistence.repository.UserJpaRepository;
import com.bescobar.notes.user.infrastructure.persistence.entity.UserEntity;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Adapter that implements both our custom UserDetailsOutputPort and Spring Security's UserDetailsService.
 * This allows us to have a clean hexagonal architecture while still being compatible with Spring Security.
 */
@Component
@AllArgsConstructor
public class UserDetailsAdapter implements UserDetailsOutputPort, UserDetailsService {

    private final UserJpaRepository userRepository;

    @Override
    public UserDetails loadUserByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        return buildUserDetails(user);
    }

    /**
     * Spring Security's UserDetailsService method.
     * Delegates to loadUserByEmail since our system uses email as the username.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // In our system, username IS the email
        return loadUserByEmail(username);
    }

    private UserDetails buildUserDetails(UserEntity user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                ))
                .build();
    }
}
