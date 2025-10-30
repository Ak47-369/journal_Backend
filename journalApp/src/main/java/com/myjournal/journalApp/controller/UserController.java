package com.myjournal.journalApp.controller;

import com.myjournal.journalApp.dto.CreateUserRequest;
import com.myjournal.journalApp.dto.UserResponse;
import com.myjournal.journalApp.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        UserResponse createdUser = userService.createUser(createUserRequest);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<UserResponse> getUserById(@AuthenticationPrincipal UserDetails userDetails) {
        String userName = userDetails.getUsername(); // Extracting userName from userDetails
        UserResponse userResponse = userService.getUserById(userService.getUserIdByName(userName));
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateUserById( @AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody CreateUserRequest createUserRequest ) {
        String userName = userDetails.getUsername();
        UserResponse updatedUser = userService.updateUserById(userService.getUserIdByName(userName), createUserRequest);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUserById(@AuthenticationPrincipal UserDetails userDetails) {
        String userName = userDetails.getUsername();
        userService.deleteUserById(userService.getUserIdByName(userName));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
