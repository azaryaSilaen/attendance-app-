package com.example.application.security;

import com.example.application.views.login.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * The `SecurityConfiguration` class is a configuration class responsible for
 * configuring security settings in the application using Spring Security. It
 * extends `VaadinWebSecurity`, indicating that it is a custom configuration
 * for securing a Vaadin web application.
 */
@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurity {

    /**
     * Configures a password encoder bean for securely encoding and verifying passwords.
     *
     * @return A `PasswordEncoder` bean instance, specifically, a `BCryptPasswordEncoder`.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures HTTP security settings, including authorization rules and the login view.
     *
     * @param http The `HttpSecurity` object to be configured.
     * @throws Exception If there is an error during configuration.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Authorize requests based on specific patterns
        http.authorizeHttpRequests(
                authorize -> authorize.requestMatchers(new AntPathRequestMatcher("/images/*.png")).permitAll());

        // Authorize requests for icons from the line-awesome addon
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(new AntPathRequestMatcher("/line-awesome/**/*.svg")).permitAll());

        // Call the superclass's configuration to ensure other security settings
        super.configure(http);

        // Set the login view for the application to be `LoginView`
        setLoginView(http, LoginView.class);
    }
}

