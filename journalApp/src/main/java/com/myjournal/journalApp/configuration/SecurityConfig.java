package com.myjournal.journalApp.configuration;

import com.myjournal.journalApp.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(request -> request
                        // Rule 1 (MOST SPECIFIC): Allow public access for user registration.
                        .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()

                        // Rule 2 (GENERAL PUBLIC): Allow access to any other designated public endpoints.
                        .requestMatchers("/public/**", "/health").permitAll()

                        // Rule 3 (GENERAL SECURED): Secure all other user endpoints (e.g., GET /api/v1/users/{id}).
                        .requestMatchers("/api/v1/users/**").authenticated()

                        // Rule 4 (OTHER SECURED): Secure all journal entry endpoints.
                        .requestMatchers("/api/v1/journal/**").authenticated()

                        // Rule 5 (ADMIN): Secure admin endpoints.
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Rule 6 (CATCH-ALL): Any other request not specified above must be authenticated.
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(AbstractHttpConfigurer::disable)// Disable form login for pure REST
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }
}
