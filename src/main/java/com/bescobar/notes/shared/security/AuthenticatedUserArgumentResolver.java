package com.bescobar.notes.shared.security;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.bescobar.notes.user.application.port.in.GetProfileInputPort;
import com.bescobar.notes.user.domain.model.User;

import lombok.AllArgsConstructor;

/**
 * Resolves @AuthenticatedUser annotation by extracting the User from the JWT token.
 * This allows controllers to directly inject the authenticated User without boilerplate code.
 *
 * Flow:
 * 1. Checks if parameter is annotated with @AuthenticatedUser
 * 2. Extracts UserDetails from SecurityContext
 * 3. Gets email from UserDetails
 * 4. Fetches and returns User from database
 */
@Component
@AllArgsConstructor
public class AuthenticatedUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final GetProfileInputPort getProfileUseCase;

    /**
     * Determines if this resolver supports the given method parameter.
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthenticatedUser.class)
                && parameter.getParameterType().equals(User.class);
    }

    /**
     * Resolves the authenticated User from the security context.
     */
    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserDetails)) {
            throw new IllegalStateException("Principal is not a UserDetails instance");
        }

        UserDetails userDetails = (UserDetails) principal;
        String email = AuthenticationHelper.extractEmail(userDetails);

        User user = getProfileUseCase.getProfileByEmail(email);

        if (user == null) {
            throw new IllegalStateException("User not found for email: " + email);
        }

        return user;
    }
}
