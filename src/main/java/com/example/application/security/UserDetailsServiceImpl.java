package com.example.application.security;

import com.example.application.data.entity.User;
import com.example.application.data.service.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The `UserDetailsServiceImpl` class is an implementation of the Spring Security
 * `UserDetailsService` interface. It is responsible for loading user details
 * from the application's database and providing these details to Spring Security
 * for authentication and authorization purposes.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructs a `UserDetailsServiceImpl` instance with the necessary dependencies.
     *
     * @param userRepository The repository for accessing user information in the database.
     */
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads user details by their username.
     *
     * @param username The username of the user to be loaded.
     * @return A `UserDetails` object containing user details.
     * @throws UsernameNotFoundException If no user with the given username is found.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Retrieve user information from the database based on the provided username
        User user = userRepository.findByUsernameIgnoreCase(username);

        if (user == null) {
            // Throw an exception if no user with the provided username is found
            throw new UsernameNotFoundException("No user present with username: " + username);
        } else {
            // Create a `UserDetails` object containing the user's username, hashed password,
            // and authorities (roles)
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getHashedPassword(),
                    getAuthorities(user)
            );
        }
    }

    /**
     * Retrieves the user's authorities (roles).
     *
     * @param user The user for whom authorities (roles) are to be retrieved.
     * @return A list of `GrantedAuthority` objects representing the user's roles.
     */
    private static List<GrantedAuthority> getAuthorities(User user) {
        // Map user roles to `GrantedAuthority` objects with the "ROLE_" prefix
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_" + user.getRoles().name()));
        /*
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

         */
    }
}

