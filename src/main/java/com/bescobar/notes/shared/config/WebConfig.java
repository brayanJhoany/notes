package com.bescobar.notes.shared.config;

import com.bescobar.notes.shared.security.AuthenticatedUserArgumentResolver;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Web MVC Configuration.
 * Registers custom argument resolvers for controller methods.
 */
@Configuration
@AllArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthenticatedUserArgumentResolver authenticatedUserArgumentResolver;

    /**
     * Adds custom argument resolvers.
     * This enables @AuthenticatedUser annotation to inject User directly into controllers.
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticatedUserArgumentResolver);
    }
}
