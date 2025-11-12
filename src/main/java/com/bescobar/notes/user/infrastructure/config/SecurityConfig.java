package com.bescobar.notes.user.infrastructure.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.bescobar.notes.shared.security.SecurityEndpointsProvider;
import com.bescobar.notes.user.infrastructure.adapter.security.JwtAuthenticationFilter;

import lombok.AllArgsConstructor;

/**
 * Global Security Configuration
 *
 * Security Philosophy: "Secure by Default"
 * - All endpoints require authentication by default
 * - Only explicitly listed endpoints in getPublicEndpoints() are public
 * - New endpoints automatically inherit security unless explicitly made public
 *
 * To add a new public endpoint:
 * 1. Create a SecurityEndpointsProvider in your module's config package
 * 2. Add the paths to the provider's getPublicEndpoints() method
 * 3. Spring will automatically collect all providers and merge endpoints
 *
 * Authentication Flow:
 * - JWT tokens in Authorization header: "Bearer <token>"
 * - Stateless sessions (no server-side session storage)
 * - Custom JWT filter validates tokens before each request
 */
@Configuration
@EnableWebSecurity
@org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity(prePostEnabled = true)
@AllArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final List<SecurityEndpointsProvider> endpointsProviders;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @SuppressWarnings("deprecation")
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // Public endpoints - explicitly defined exceptions
                .requestMatchers(getPublicEndpoints()).permitAll()

                // Everything else requires authentication (secure by default)
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Dynamically collects public endpoints from all modules.
     * Each module provides its own public endpoints via SecurityEndpointsProvider.
     */
    private String[] getPublicEndpoints() {
        List<String> allEndpoints = new ArrayList<>();

        // Collect endpoints from all modules
        for (SecurityEndpointsProvider provider : endpointsProviders) {
            allEndpoints.addAll(Arrays.asList(provider.getPublicEndpoints()));
        }

        // Add global public endpoints
        allEndpoints.add("/actuator/health");

        return allEndpoints.toArray(new String[0]);
    }
}