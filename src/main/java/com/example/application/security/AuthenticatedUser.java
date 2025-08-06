package com.example.application.security;

import com.example.application.data.entity.User;
import com.example.application.data.service.UserRepository;
import com.vaadin.flow.spring.security.AuthenticationContext;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * The `AuthenticatedUser` component manages user authentication and provides
 * methods for retrieving authenticated user information and performing logout.
 * It interfaces with the `UserRepository` and `AuthenticationContext` to handle
 * user-related operations.
 */
@Component
public class AuthenticatedUser {

    private final UserRepository userRepository;
    private final AuthenticationContext authenticationContext;

    /**
     * Constructs an `AuthenticatedUser` instance with the necessary dependencies.
     *
     * @param authenticationContext The authentication context responsible for
     *                             managing user authentication and session.
     * @param userRepository       The repository for accessing user information.
     */
    public AuthenticatedUser(AuthenticationContext authenticationContext, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.authenticationContext = authenticationContext;
    }

    /**
     * Retrieves the authenticated user information.
     *
     * @return An `Optional` containing the authenticated user if available,
     *         or an empty `Optional` if no user is authenticated.
     */
    @Transactional
    public Optional<User> get() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .map(userDetails -> userRepository.findByUsernameIgnoreCase(userDetails.getUsername()));
    }

    /**
     * Logs out the authenticated user, effectively ending their session.
     */
    public void logout() {
        authenticationContext.logout();
    }
}

