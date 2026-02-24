package com.bescobar.notes.user.infrastructure.config.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark endpoints as public (no authentication required).
 *
 * Usage:
 * @PublicEndpoint
 * @PostMapping("/register")
 * public ResponseEntity<AuthResponse> register() { ... }
 *
 * Note: For now, this is just documentation. The actual security configuration
 * is still centralized in SecurityConfig.getPublicEndpoints().
 *
 * Future improvement: Create a processor to automatically detect these annotations
 * and configure security accordingly.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PublicEndpoint {

    /**
     * Optional description of why this endpoint is public
     */
    String reason() default "";
}
