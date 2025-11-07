package com.myjournal.journalApp.configuration;

import com.myjournal.journalApp.configuration.filter.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Value;
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

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    @Value("${management.endpoints.web.exposure.include:health}")
    private String[] exposedActuatorEndpoints;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
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
        List<String> publicUrls = new ArrayList<>();
        // Add static public URLs
        publicUrls.add("/api/v1/auth/**");
        publicUrls.add("/public/**");
        publicUrls.add("/api-docs/**");

        // Add the dynamically loaded actuator endpoints
        if (exposedActuatorEndpoints != null) {
            for (String endpoint : exposedActuatorEndpoints) {
                if (endpoint.equals("*")) {
                    // Expose all actuator endpoints
                    publicUrls.add("/actuator/**");
                    break; // No need to add more
                }
                publicUrls.add("/actuator/" + endpoint);
            }
        }

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        // Rule 1: Allow public access to our dynamically built list
                        .requestMatchers(publicUrls.toArray(new String[0])).permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Rule 2 (CATCH-ALL): Any other request must be authenticated.
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
