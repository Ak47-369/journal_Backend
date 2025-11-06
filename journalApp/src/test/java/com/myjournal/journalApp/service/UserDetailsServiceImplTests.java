package com.myjournal.journalApp.service;

import com.myjournal.journalApp.entity.User;
import com.myjournal.journalApp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTests {

    @Mock
    private UserRepository userRepository; // We mock the dependency

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService; // We test this service

    private User testUser;

    @BeforeEach
    void setUp() {
        // Create a realistic User entity that the repository would return
        testUser = new User("ram", "hashed_password_from_db","email@gmail.com");
    }

    @Test
    @DisplayName("Should load user details successfully when user exists")
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        // Arrange: Configure the mock repository
        // When findByUserName is called with any string, return our testUser wrapped in an Optional.
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.of(testUser));

        // Act: Call the method we are testing
        UserDetails userDetails = userDetailsService.loadUserByUsername("ram");

        // Assert: Verify the results
        assertNotNull(userDetails);
        assertEquals(testUser.getUserName(), userDetails.getUsername());
        assertEquals(testUser.getPassword(), userDetails.getPassword());
        assertFalse(userDetails.getAuthorities().isEmpty());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when user does not exist")
    void loadUserByUsername_UserDoesNotExist_ThrowsException() {
        // Arrange: Configure the mock repository to return an empty Optional
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.empty());
        // Act & Assert: Verify that the correct exception is thrown
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistent_user");
        });
    }
}
