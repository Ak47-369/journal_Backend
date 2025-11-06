package com.myjournal.journalApp.configuration;

import com.myjournal.journalApp.configuration.filter.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthFilter JwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        JwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(request -> request
                        // Rule 1 (MOST SPECIFIC): Allow public access for user registration.
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/public/**", "/actuator/health").permitAll()
                        // Rule 5 (ADMIN): Secure admin endpoints.
                        .requestMatchers("/admin/**", "/actuator/**").hasRole("ADMIN")

                        // Rule 6 (CATCH-ALL): Any other request not specified above must be authenticated.
                        .anyRequest().authenticated()
                )
                // Configure session management to be Stateless
                .sessionManagement(session -> session.sessionCreationPolicy((SessionCreationPolicy.STATELESS)))
                // Add out Custom JWT filter before the standard authentication filter
                .addFilterBefore(JwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }
}
